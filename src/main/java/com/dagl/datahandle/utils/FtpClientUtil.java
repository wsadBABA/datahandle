package com.dagl.datahandle.utils;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author zjc
 * 2018.8.10
 */
@Component
public class FtpClientUtil {

    private static String ftpIp;

    private static String ftpPort;

    private static String ftpName;

    private static String ftpPassWord;

    private static String ftpPath;

    @Value("${ftpIp}")
    public void setFtpIp(String ftpIp) {
        FtpClientUtil.ftpIp = ftpIp;
    }
    @Value("${ftpPort}")
    public void setFtpPort(String ftpPort) {
        FtpClientUtil.ftpPort = ftpPort;
    }
    @Value("${ftpName}")
    public void setFtpName(String ftpName) {
        FtpClientUtil.ftpName = ftpName;
    }
    @Value("${ftpPassWord}")
    public void setFtpPassWord(String ftpPassWord) {
        FtpClientUtil.ftpPassWord = ftpPassWord;
    }
    @Value("${ftpPath}")
    public void setFtpPath(String ftpPath) {
        FtpClientUtil.ftpPath = ftpPath;
    }

    public static FTPClient getInstance() {
        FTPClient ftpClient = new FTPClient();
        String localCharset = "GBK";
        try {
            ftpClient.connect(ftpIp);    //两个参数，第一个ip，第二个端口；因为为默认端口21，可以不写
            boolean result;
            result = ftpClient.login(ftpName, ftpPassWord);      //账号、密码
            if (result || ftpClient.isConnected()) {
                // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
                    localCharset = "UTF-8";
                }
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);  //二进制(图片等格式的文件必须以二进制传输)
                ftpClient.enterLocalPassiveMode();            //让本地分配一个端口用于文件传输
                ftpClient.setControlEncoding(localCharset);
                ftpClient.setBufferSize(1024 * 1024 * 2);
                ftpClient.setDataTimeout(60000);       //设置传输超时时间为60秒
                return ftpClient;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void logout(FTPClient ftpClient) {
        if (ftpClient != null) {
            try {
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        ftpClient = null;
                    }
                }
            }
        }
    }

    /**
     * 将图片从NAS上下载到本地
     *
     * @param remoteDir 在远端上的地址
     * @param localDir  要下载到的本地地址
     * @throws Exception
     */
    public static void downToLocal(String remoteDir, String localDir) throws Exception {
        //如果照片存在本地，则不会重复下载
        if (existInLocal(localDir)) {
            return;
        }
        FTPClient ftpClient = FtpClientUtil.getInstance();
        try (
                OutputStream os = new FileOutputStream(new File(localDir));
                InputStream fis = ftpClient.retrieveFileStream(remoteDir);
        ) {
            int len = 0;
            byte[] cache = new byte[1 << 10];
            // 开始读取
            while ((len = fis.read(cache)) != -1) {
                os.write(cache, 0, len);
            }
        } catch (NullPointerException e) {

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FtpClientUtil.logout(ftpClient);
        }
    }

    public static boolean existInLocal(String localDir) {
        return new File(localDir).exists();
    }


}
