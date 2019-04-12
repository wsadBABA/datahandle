package com.dagl.datahandle.service;

/**
 * @author Administrator
 * @className ExcelService
 * @description excel解析
 * @date 2018/10/29
 */

import com.dagl.datahandle.config.DaConfig;
import com.dagl.datahandle.dao.SjblDao;
import com.dagl.datahandle.enums.CellDataType;
import com.dagl.datahandle.utils.FtpClientUtil;
import com.dagl.datahandle.utils.MapUtil;
import com.dagl.datahandle.utils.StringUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.*;

/*********************************************** excel解析 *************************************************/
@Service
public class ExcelService {
    Logger logger = LoggerFactory.getLogger(ExcelService.class);

    @Autowired
    private SjblDao sjblDao;
    @Autowired
    private DaConfig daConfig;

    private StylesTable stylesTable;
    public String rId;
    public String nasPath = "";
    public String localFilePath = "";
    public String type = "";
    /**
     * 成功导入数量
     */
    public Integer sucNum = 0;
    /**
     * 导入失败数量
     */
    public Integer defNum = 0;

    /**
     * 失败反馈写入
     */
    BufferedWriter defWriter = null;


    /**
     * 初始化
     *
     * @param localFjPath
     * @param nasPath
     * @param rId
     * @param defWriter
     */
    public void excelServiceInit(String localFjPath, String nasPath,
                                 String rId, BufferedWriter defWriter, String type) {
        this.sucNum = 0;
        this.defNum = 0;
        this.localFilePath = localFjPath;
        this.nasPath = nasPath;
        this.rId = rId;
        this.defWriter = defWriter;
        this.type = type;
    }


    /**
     * 处理一个sheet
     *
     * @param filename
     * @throws Exception
     */
    public void processOneSheet(String filename) throws Exception {

        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        stylesTable = r.getStylesTable();
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        // Seems to either be rId# or rSheet#
        InputStream sheet2 = r.getSheet(rId);
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }

    /**
     * 处理所有sheet
     *
     * @param filename
     * @throws Exception
     */
    public void processAllSheets(String filename) throws Exception {

        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
            System.out.println("");
        }
    }

    /**
     * 获取解析器
     *
     * @param sst
     * @return
     * @throws SAXException
     */
    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
                XMLReaderFactory.createXMLReader(
                        "org.apache.xerces.parsers.SAXParser"
                );
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * 自定义解析处理器
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;

        private List<String> rowlist = new ArrayList<String>();
        /**
         * 行
         */
        private int curRow = 0;
        /**
         * 列
         */
        private int curCol = 0;

        /**
         * 定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
         */
        private String preRef = null, ref = null;
        /**
         * 定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格
         */
        private String maxRef = null;

        private CellDataType nextDataType = CellDataType.SSTINDEX;
        private final DataFormatter formatter = new DataFormatter();
        private short formatIndex;
        private String formatString;

        private FTPClient ftpClient;


        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        /**
         * 解析一个element的开始时触发事件
         */
        @Override
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {

            // c => cell
            if ("c".equals(name)) {
                //前一个单元格的位置
                if (preRef == null) {
                    preRef = attributes.getValue("r");
                } else {
                    preRef = ref;
                }
                //当前单元格的位置
                ref = attributes.getValue("r");

                this.setNextDataType(attributes);

                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                if (cellType != null && "s".equals(cellType)) {
                    nextIsString = true;
                } else {
                    nextIsString = false;
                }

            }
            // Clear contents cache
            lastContents = "";
        }

        /**
         * 根据element属性设置数据类型
         *
         * @param attributes
         */
        public void setNextDataType(Attributes attributes) {

            nextDataType = CellDataType.NUMBER;
            formatIndex = -1;
            formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType)) {
                nextDataType = CellDataType.BOOL;
            } else if ("e".equals(cellType)) {
                nextDataType = CellDataType.ERROR;
            } else if ("inlineStr".equals(cellType)) {
                nextDataType = CellDataType.INLINESTR;
            } else if ("s".equals(cellType)) {
                nextDataType = CellDataType.SSTINDEX;
            } else if ("str".equals(cellType)) {
                nextDataType = CellDataType.FORMULA;
            }
            if (cellStyleStr != null) {
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                formatIndex = style.getDataFormat();
                formatString = style.getDataFormatString();
                if ("m/d/yy" == formatString) {
                    nextDataType = CellDataType.DATE;
                    //full format is "yyyy-MM-dd hh:mm:ss.SSS";
                    formatString = "yyyy-MM-dd";
                }
                if (formatString == null) {
                    nextDataType = CellDataType.NULL;
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
            }
        }

        /**
         * 解析一个element元素结束时触发事件
         */
        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // 根据sst索引值获取单元格真正存储的字符串.
            // Do now, as characters() may be called more than once
            if (nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                nextIsString = false;
            }

            // v代表单元格；可将xlsx文件改为rar文件，使用压缩原件查看xlsx底层xml存储和结构
            // Output after we've seen the string contents
            if ("v".equals(name)) {
                String value = this.getDataValue(lastContents.trim(), "");
                //补全单元格之间的空单元格
                if (!ref.equals(preRef)) {
                    int len = countNullCell(ref, preRef);
                    for (int i = 0; i < len; i++) {
                        rowlist.add(curCol, "");
                        curCol++;
                    }
                }
                rowlist.add(curCol, value);
                curCol++;
            } else {
                //如果标签名称为 row，这说明已到行尾，调用 optRows() 方法
                if ("row".equals(name)) {
                    String value = "";
                    //默认第一行为表头，以该行单元格数目为最大数目
                    if (curRow == 0) {
                        maxRef = ref;
                    }
                    //补全一行尾部可能缺失的单元格
                    if (maxRef != null) {
                        int len = countNullCell(maxRef, ref);
                        for (int i = 0; i <= len; i++) {
                            rowlist.add(curCol, "");
                            curCol++;
                        }
                    }
                    /**************** 业务处理 ***************/
                    Map map;
                    try {
                        if ("yj".equals(type)) {
                            if (curRow == 0) {
                                ftpClient = getFTPClient();
                            } else {
                                map = MapUtil.arrDataToMap(rowlist, daConfig.getYjExcelFields().split(","),daConfig.getDateForm());
                                if (sjblDao.isDataNotExist(map, daConfig.getYjUniqueField(), daConfig.getYjTemTable())) {
                                    ftpClient = ftpCheck(ftpClient);
                                    map.put("NASFilePath",yjToNasByFtp(map.get(daConfig.getYjName()).toString(), ftpClient));
                                    sjblDao.insertToZjb(map,
                                            daConfig.getYjExcelFields().replaceAll(":date",""),
                                            daConfig.getYjTemTable(),"yj");
                                }
                            }
                        } else if ("yw".equals(type)) { //业务
                            if (curRow != 0) {
                                map = MapUtil.arrDataToMap(rowlist, daConfig.getYwExcelFields().split(","),daConfig.getDateForm());
                                if (sjblDao.isDataNotExist(map, daConfig.getYwUniqueField(), daConfig.getYwTemTable())) {
                                    sjblDao.insertToZjb(map,
                                            daConfig.getYwExcelFields().replaceAll(":date",""),
                                            daConfig.getYwTemTable(),"yw");
                                }
                            }
                        } else { //案卷
                            if (curRow != 0) {
                                map = MapUtil.arrDataToMap(rowlist, daConfig.getAjExcelFields().split(","),daConfig.getDateForm());
                                if (sjblDao.isDataNotExist(map, daConfig.getAjUniqueField(), daConfig.getAjTemTable())) {
                                    sjblDao.insertToZjb(map,
                                            daConfig.getYwExcelFields().replaceAll(":date",""),
                                            daConfig.getYwTemTable(),"aj");
                                }
                            }
                        }

                        if (curRow != 0) { //记录成功数量
                            sucNum++;
                            System.out.println(curRow);
                        }
                    } catch (Exception e) {
                        defRecord(rowlist, e);
                        e.printStackTrace();
                        defNum++;
                        if (StringUtil.isNotNull(e.getMessage())) {
                            if ("NasLoadError".equals(e.getMessage())) { //NAS服务器登陆或工作空间切换异常
                                throw new RuntimeException("NasLoadError");
                            }
                        }
                    }
                    /****************  业务处理  ***************/


                    curRow++;
                    //一行的末尾重置一些数据
                    rowlist.clear();
                    curCol = 0;
                    preRef = null;
                    ref = null;
                }
            }
        }

        /***
         * 文档解析结束后处理 关闭ftpClient与defWriter
         */
        @Override
        public void endDocument() {
            try {
                if (StringUtil.isNotNull(defWriter)) {
                    defWriter.close();
                }
            } catch (Exception r) {
            }
            try {
                if (StringUtil.isNotNull(ftpClient)) {
                    closeFTPClient(ftpClient);
                }
            } catch (Exception r) {
            }
        }

        /**
         * 根据数据类型获取数据
         *
         * @param value
         * @param thisStr
         * @return
         */
        public String getDataValue(String value, String thisStr)

        {
            switch (nextDataType) {
                //这几个的顺序不能随便交换，交换了很可能会导致数据错误
                case BOOL:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR:
                    thisStr = "\"ERROR:" + value.toString() + '"';
                    break;
                case FORMULA:
                    thisStr = '"' + value.toString() + '"';
                    break;
                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    thisStr = rtsi.toString();
                    rtsi = null;
                    break;
                case SSTINDEX:
                    String sstIndex = value.toString();
                    thisStr = value.toString();
                    break;
                case NUMBER:
                    if (formatString != null) {
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
                    } else {
                        thisStr = value;
                    }
                    thisStr = thisStr.replace("_", "").trim();
                    break;
                case DATE:
                    try {
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                    } catch (NumberFormatException ex) {
                        thisStr = value.toString();
                    }
                    thisStr = thisStr.replace(" ", "");
                    break;
                default:
                    thisStr = "";
                    break;
            }
            return thisStr;
        }

        /**
         * 获取element的文本数据
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            lastContents += new String(ch, start, length);
        }

        /**
         * 计算两个单元格之间的单元格数目(同一行)
         *
         * @param ref
         * @param preRef
         * @return
         */
        public int countNullCell(String ref, String preRef) {
            //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
            String xfd = ref.replaceAll("\\d+", "");
            String xfdSec = preRef.replaceAll("\\d+", "");

            xfd = fillChar(xfd, 3, '@', true);
            xfdSec = fillChar(xfdSec, 3, '@', true);

            char[] letter = xfd.toCharArray();
            char[] letterSec = xfdSec.toCharArray();
            int res = (letter[0] - letterSec[0]) * 26 * 26 + (letter[1] - letterSec[1]) * 26 + (letter[2] - letterSec[2]);
            return res - 1;
        }

        /**
         * 字符串的填充
         *
         * @param str
         * @param len
         * @param let
         * @param isPre
         * @return
         */
        String fillChar(String str, int len, char let, boolean isPre) {
            int len_1 = str.length();
            if (len_1 < len) {
                if (isPre) {
                    for (int i = 0; i < (len - len_1); i++) {
                        str = let + str;
                    }
                } else {
                    for (int i = 0; i < (len - len_1); i++) {
                        str = str + let;
                    }
                }
            }
            return str;
        }
    }

    /***************************************  业务  **************************************/
    /**
     * 记录异常数据
     *
     * @param rowlist
     */
    private void defRecord(List<String> rowlist, Exception ee) {
        //拼接一行的数据
        String value = "";
        for (int i = 0; i < rowlist.size(); i++) {
            if (rowlist.get(i).contains(",")) {
                value += "\"" + rowlist.get(i) + "\",";
            } else {
                value += rowlist.get(i) + ",";
            }
        }
        //加换行符
        value += ee.getLocalizedMessage();
        try {
            defWriter.write(value);
            defWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * NAS连接
     *
     * @return
     */
    private FTPClient getFTPClient() {
        FTPClient ftpClient;
        try {
            ftpClient = FtpClientUtil.getInstance();
            ftpClient.changeWorkingDirectory(nasPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("NasLoadError"); //NAS服务器登陆或工作空间切换异常
        }
        return ftpClient;
    }

    /**
     * ftp校验
     *
     * @param ftpClient
     */
    private FTPClient ftpCheck(FTPClient ftpClient) {
        try {
            ftpClient.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            logout(ftpClient);
            ftpClient = getFTPClient();
        }
        return ftpClient;
    }

    private void logout(FTPClient ftpClient) {
        FtpClientUtil.logout(ftpClient);
    }

    /**
     * NAS连接关闭
     *
     * @param ftpClient
     */
    private void closeFTPClient(FTPClient ftpClient) {
        try {
            ftpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("NasDisError");//NAS服务器连接关闭
        }
    }

    /**
     * Excel导入中间表时，将对应的原件上传到NAS服务器
     *
     * @param ftpClient
     * @throws Exception
     */
    private String yjToNasByFtp(String mc, FTPClient ftpClient) throws Exception {
        mc = mc.replaceAll(" ", "").replaceAll("]", "");
        String nasName = "";
        File file = getFile(mc);//根据文件名称获取File
        UUID uuid = UUID.randomUUID();
        nasName = uuid + mc.substring(mc.lastIndexOf("."));
        InputStream is = new FileInputStream(file);     //获取文件输入流
        boolean result = ftpClient.appendFile(nasName, is); //上传文件
        is.close();                                     //关闭流
        if (!result) {
            nasName = "";
        }
        return nasPath + "/" + nasName;
    }


    /***
     * 根据名称组成中的案卷编号以及路径获取指定名称文原件
     * @param mc
     * @return
     */
    public File getFile(String mc) {
        String fileName;
        String filePath = mc.substring(0, mc.lastIndexOf("_"));
        File yj = new File(localFilePath + filePath + "/" + mc);
        if (!yj.exists()) {
            File[] files = new File(localFilePath).listFiles();
            for (File a : files) {
                fileName = a.getName();
                if (fileName.contains(" ")) {
                    fileName = fileName.substring(0, fileName.indexOf(" "));
                }
                if (fileName.equals(filePath)) {
                    yj = new File(a.getAbsolutePath() + "/" + mc);
                    break;
                }
            }
        }
        return yj;
    }
}