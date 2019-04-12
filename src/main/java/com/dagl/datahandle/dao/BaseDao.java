package com.dagl.datahandle.dao;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Reader;
import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * @author Administrator
 * @className BaseDao
 * @description TODO
 * @date 2019/03/22
 */
@Repository
public class BaseDao{
    //数据层模板
    @Autowired
//    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private SessionFactory sessionFactory;

    /**
     * SQL语句操作部分
     */

    /**
     * @param sql SQL语句
     * @param params 各绑定变量参数
     * @return Object结果集
     */
    public List queryArrayList(String sql, Object... params) {
        return createSQLQuery(sql, params).list();
    }

    /**
     * @param sql SQL语句
     * @param params 参数集合
     * @return Object结果集
     */
    public List queryArrayList(String sql, List params) {
        return createSQLQuery(sql, params).list();
    }

    /**
     * @param sql SQL语句
     * @param params 参数键值对
     * @return Object结果集
     */
    public List queryArrayList(String sql, Map<String, Object> params) {
        return createSQLQuery(sql, params).list();
    }

    /**
     * @param sql SQL语句
     * @param params 各绑定变量参数
     * @return Map集合（KEY为大写数据库字段名）
     */
    public List queryMapList(String sql, Object... params) {
        return createSQLQuery(sql, params).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * @param sql SQL语句
     * @param params 各绑定变量参数
     * @return Map集合（KEY为大写数据库字段名）
     */
    public List queryMapList(String sql, List params) {
        return createSQLQuery(sql, params).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * @param sql SQL语句
     * @param params 参数键值对
     * @return Map集合（KEY为大写数据库字段名）
     */
    public List queryMapList(String sql, Map<String, Object> params) {
        return createSQLQuery(sql, params).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * @param sql SQL语句
     * @param clobs CLOB类型数组
     * @param params 参数对象
     * @return Map集合
     */
    public List queryClobMapList(String sql, final String[] clobs, Object... params) {
        return createSQLQuery(sql, params).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).list();
    }

    /**
     * @param sql SQL语句
     * @param clobs CLOB类型数组
     * @param params 参数列表
     * @return Map集合
     */
    public List queryClobMapList(String sql, final String[] clobs, List params) {
        return createSQLQuery(sql, params).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).list();
    }

    /**
     * @param sql SQL语句
     * @param clobs CLOB类型数组
     * @param params 参数键值对
     * @return Map集合
     */
    public List queryClobMapList(String sql, final String[] clobs, Map<String, Object> params) {
        return createSQLQuery(sql, params).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数对象
     * @return 分页列表
     */
    public List queryArrayListForPage(String sql, int pageNo, int pageSize, Object... params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数对象
     * @return 分页列表
     */
    public List queryArrayListForPage(String sql, int pageNo, int pageSize, List params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数键值对
     * @return 分页列表
     */
    public List queryArrayListForPage(String sql, int pageNo, int pageSize, Map<String, Object> params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数键值对
     * @return 分页列表
     */
    public List queryMapListForPage(String sql, int pageNo, int pageSize, Object... params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数列表
     * @return 分页列表
     */
    public List queryMapListForPage(String sql, int pageNo, int pageSize, List params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数键值对
     * @return 分页列表
     */
    public List queryMapListForPage(String sql, int pageNo, int pageSize, Map<String, Object> params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数键值对
     * @return 分页列表
     */
    public List queryClobMapListForPage(String sql, int pageNo, int pageSize, final String[] clobs, Object... params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数列表
     * @return 分页(含Clob类型)Map集合
     */
    public List queryClobMapListForPage(String sql, int pageNo, int pageSize, final String[] clobs, List params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).list();
    }

    /**
     * 分页SQL查询
     * @param sql SQL语句
     * @param pageNo 页码
     * @param pageSize 每页数据个数
     * @param params 参数键值对
     * @return 分页(含Clob类型)Map集合
     */
    public List queryClobMapListForPage(String sql, int pageNo, int pageSize, final String[] clobs, Map<String, Object> params) {
        return createSQLQuery(sql, params).setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).list();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量参数
     * @return 一个实体
     */
    public Object queryUniqueResult(String sql, Object... params) {
        return createSQLQuery(sql, params).uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量列表
     * @return 一个实体
     */
    public Object queryUniqueResult(String sql, List params) {
        return createSQLQuery(sql, params).uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量键值对
     * @return 一个实体
     */
    public Object queryUniqueResult(String sql, Map<String, Object> params) {
        return createSQLQuery(sql, params).uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量参数
     * @return 一个实体的键值对
     */
    public Map<String, Object> queryUniqueMapResult(String sql, Object... params) {
        return (Map<String, Object>)createSQLQuery(sql, params).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量列表
     * @return 一个实体的键值对
     */
    public Map<String, Object> queryUniqueMapResult(String sql, List params) {
        return (Map<String, Object>)createSQLQuery(sql, params).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量键值对
     * @return 一个实体的键值对
     */
    public Map<String, Object> queryUniqueMapResult(String sql, Map<String, Object> params) {
        return (Map<String, Object>)createSQLQuery(sql, params).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量参数
     * @return 一个Clob类型实体的键值对
     */
    public Map<String, Object> queryUniqueClobMapResult(String sql, final String[] clobs, Object... params) {
        return (Map<String, Object>)createSQLQuery(sql, params).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量列表
     * @return 一个Clob类型实体的键值对
     */
    public Map<String, Object> queryUniqueClobMapResult(String sql, final String[] clobs, List params) {
        return (Map<String, Object>)createSQLQuery(sql, params).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量键值对
     * @return 一个Clob类型实体的键值对
     */
    public Map<String, Object> queryUniqueClobMapResult(String sql, final String[] clobs, Map<String, Object> params) {
        return (Map<String, Object>)createSQLQuery(sql, params).setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] values, String[] columns) {
                return transformClob(values, columns, clobs);
            }
            @Override
            public List transformList(List list) {
                return list;
            }
        }).uniqueResult();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量参数
     */
    public void excuteSql(String sql, Object... params) {
        createSQLQuery(sql, params).executeUpdate();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量列表
     */
    public void excuteSql(String sql, List params) {
        createSQLQuery(sql, params).executeUpdate();
    }

    /**
     * @param sql sql语句
     * @param params 各绑定变量键值对
     */
    public void excuteSql(String sql, Map<String, Object> params) {
        createSQLQuery(sql, params).executeUpdate();
    }

    /**
     * 获取当前数据库时间
     * @return TIMESTAMP类型时间
     */
    public Date getDbCurrentTime() {
        return (Date)createSQLQuery("select sysdate from dual").addScalar("sysdate", StandardBasicTypes.TIMESTAMP)
                .uniqueResult();
    }

    /**
     * 获取当前数据库时间
     * @return DATE类型时间
     */
    public Date getDbCurrentDate() {
        return (Date)createSQLQuery("select sysdate from dual").addScalar("sysdate", StandardBasicTypes.DATE)
                .uniqueResult();
    }

    /**
     * 执行存储过程
     *
     * @param sql 存储过程语句，如{call proc(?,?,?)}
     * @param inParams 传入参数，key是传入参数的位置，value是参数值
     * @param outParams 传出参数，key是传出参数的位置，value是传出参数的类型，如OracleTypes.CURSOR
     * @return 返回参数封装为MAP形式，key是参数的位置，跟outParams的key一致，value是参数值，如果是集合类型，则该参数封装为List<Map<String,Object>>类型
     */
    public Map<Integer, Object> callProcedure(final String sql, final Map<Integer, Object> inParams, final Map<Integer, Integer> outParams) {
        return (Map<Integer, Object>)jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                CallableStatement st = connection.prepareCall(sql);
                //设置传入参数
                if (inParams != null && !inParams.isEmpty()) {
                    Object value = null;
                    for (Map.Entry<Integer, Object> en : inParams.entrySet()) {
                        value = en.getValue();
                        if(value != null && value instanceof Date){
                            st.setObject(en.getKey(), new java.sql.Date(((Date)value).getTime()));
                        }else{
                            st.setObject(en.getKey(), en.getValue());
                        }
                    }
                }
                //设置传出参数
                if (outParams != null && !outParams.isEmpty()) {
                    for (Map.Entry<Integer, Integer> en : outParams.entrySet()) {
                        st.registerOutParameter(en.getKey(), en.getValue());
                    }
                }
                return st;
            }
        },new CallableStatementCallback<Object>() {
            @Override
            public Object doInCallableStatement(CallableStatement st) throws SQLException, DataAccessException {
                //传出参数封装成MAP形式返回，位置跟outParams的key一致
                Map<Integer, Object> resultMap = null;
                //执行存储过程
                st.execute();
                //封装结果集
                if (outParams != null && !outParams.isEmpty()) {
                    resultMap = new HashMap<Integer, Object>();
                    //非结果集类型的传出参数
                    Object result = null;
                    //结果集类型的传出参数,封装成MAP的集合形式输出
                    ResultSet rs = null;
                    ResultSetMetaData meteData = null;
                    Integer columnLen = 0;
                    String filedName = null;
                    Map<String, Object> map = null;
                    List<Map<String, Object>> mapList = null;
                    //循环迭代传出参数
                    for (Map.Entry<Integer, Integer> en : outParams.entrySet()) {
                        result = st.getObject(en.getKey());
                        if (result instanceof ResultSet) {
                            //结果集类型
                            rs = (ResultSet) result;
                            //获取列总数
                            meteData = rs.getMetaData();
                            columnLen = meteData.getColumnCount();
                            mapList = new ArrayList<Map<String, Object>>();
                            while (rs.next()) {
                                map = new HashMap<String, Object>();
                                for (int i = 0; i < columnLen; i++) {
                                    filedName = meteData.getColumnName(i + 1);
                                    map.put(filedName.toUpperCase(), rs.getObject(filedName));
                                }
                                mapList.add(map);
                            }
                            resultMap.put(en.getKey(), mapList);
                        } else {
                            //非结果集类型
                            resultMap.put(en.getKey(), result);
                        }
                    }
                }
                return resultMap;
            }
        });
    }

    /**
     * 公用操作部分
     */
    /**
     * 获取数据库session
     * @return session
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * @param hql HQL语句
     * @param params 各绑定变量参数
     * @return 查询结果
     */
    public Query createQuery(String hql, Object... params) {
        Query query = getSession().createQuery(hql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if(params[i] == null){
                    query.setParameter(i, "");
                }else{
                    query.setParameter(i, params[i]);
                }
            }
        }
        return query;
    }

    /**
     * @param hql HQL语句
     * @param params 各绑定变量列表
     * @return 查询结果
     */
    public Query createQuery(String hql, List params) {
        Query query = getSession().createQuery(hql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                if(params.get(i) == null){
                    query.setParameter(i, "");
                }else{
                    query.setParameter(i, params.get(i));
                }
            }
        }
        return query;
    }

    /**
     * @param hql HQL语句
     * @param params 各绑定变量键值对
     * @return 查询结果
     */
    public Query createQuery(String hql, Map<String, Object> params) {
        Query query = getSession().createQuery(hql);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> en : params.entrySet()) {
                if (en.getValue() instanceof Collection) {
                    query.setParameterList(en.getKey(), (Collection) en.getValue());
                } else if (en.getValue() instanceof Object[]) {
                    query.setParameterList(en.getKey(), (Object[]) en.getValue());
                } else {
                    if(en.getValue() == null){
                        query.setParameter(en.getKey(), "");
                    }else{
                        query.setParameter(en.getKey(), en.getValue());
                    }
                }
            }
        }
        return query;
    }

    /**
     * @param sql SQL语句
     * @param params 各绑定变量参数
     * @return 查询结果
     */
    public SQLQuery createSQLQuery(String sql, Object... params) {
        SQLQuery sqlQuery = getSession().createSQLQuery(sql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if(params[i] == null){
                    sqlQuery.setParameter(i, "");
                }else{
                    sqlQuery.setParameter(i, params[i]);
                }
            }
        }
        return sqlQuery;
    }

    /**
     * @param sql SQL语句
     * @param params 各绑定变量列表
     * @return 查询结果
     */
    public SQLQuery createSQLQuery(String sql, List params) {
        SQLQuery sqlQuery = getSession().createSQLQuery(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                if(params.get(i) == null){
                    sqlQuery.setParameter(i, "");
                }else{
                    sqlQuery.setParameter(i, params.get(i));
                }
            }
        }
        return sqlQuery;
    }

    /**
     * @param sql SQL语句
     * @param params 各绑定变量键值对
     * @return 查询结果
     */
    public SQLQuery createSQLQuery(String sql, Map<String, Object> params) {
        SQLQuery sqlQuery = getSession().createSQLQuery(sql);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> en : params.entrySet()) {
                if (en.getValue() instanceof Collection) {
                    sqlQuery.setParameterList(en.getKey(), (Collection) en.getValue());
                } else if (en.getValue() instanceof Object[]) {
                    sqlQuery.setParameterList(en.getKey(), (Object[]) en.getValue());
                } else {
                    if(en.getValue() == null){
                        sqlQuery.setParameter(en.getKey(), "");
                    }else{
                        sqlQuery.setParameter(en.getKey(), en.getValue());
                    }
                }
            }
        }
        return sqlQuery;
    }

    /**
     * 将CLOB转为键值对
     * @param values 值数组
     * @param columns 列数组
     * @param clobs CLOB数组
     * @return 查询结果键值对
     */
    public Map<String, Object> transformClob(Object[] values, String[] columns, String[] clobs){
        Map<String, Object> map = new HashMap<String, Object>();
        if (columns != null && columns.length > 0 && clobs != null && clobs.length > 0) {
            for (int i = 0; i < columns.length; i++) {
                if (ArrayUtils.contains(clobs, columns[i])) {
                    map.put(columns[i], clobToStr(values[i]));
                } else {
                    map.put(columns[i], values[i]);
                }
            }
        }
        return map;
    }

    /**
     * 将CLOB转为字符串
     * @param obj CLOB对象
     * @return 字符串
     */
    public String clobToStr(Object obj) {
        Reader reader = null;
        try {
            if (obj != null && obj instanceof Clob) {
                Clob clob = (Clob) obj;
                reader = clob.getCharacterStream();
                char[] chars = new char[(int) clob.length()];
                reader.read(chars);
                return new String(chars);
            } else if (obj != null && obj instanceof String) {
                return obj.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 以下为JdbcTemplate和HibernateTemplate的set和get方法
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}