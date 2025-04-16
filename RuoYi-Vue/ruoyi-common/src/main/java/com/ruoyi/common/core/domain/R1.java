package com.ruoyi.common.core.domain;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.enums.RespEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R1 extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R1 setData(Object data) {
        put("data",data);
        return this;
    }

    //利用fastjson进行反序列化
    public <T> T getData(Class<T> typeReference) {
        Object data = get("data");	//默认是map
        String jsonString = JSON.toJSONString(data);
        T t = JSON.parseObject(jsonString, typeReference);
        return t;
    }

    public R1() {
        put("code", 200);
        put("msg", "success");
    }

    public static R1 error() {
        return error(5000, "未知异常，请联系管理员");
    }

    public static R1 error(String msg) {
        return error(500, msg);
    }
    public static R1 error(RespEnum respEnum) {
        return error(respEnum.getCode(), respEnum.getMessage());
    }
    public static R1 error(int code, String msg) {
        R1 r1 = new R1();
        r1.put("code", code);
        r1.put("msg", msg);
        return r1;
    }
    public static R1 ok(RespEnum respEnum) {
        return ok(respEnum.getCode(), respEnum.getMessage());
    }
    public static R1 ok(String msg) {
        R1 r1 = new R1();
        r1.put("msg", msg);
        return r1;
    }
    public static R1 ok(int code, String msg) {
        R1 r1 = new R1();
        r1.put("code", code);
        r1.put("msg", msg);
        return r1;
    }
    public static R1 ok(Map<String, Object> map) {
        R1 r1 = new R1();
        r1.putAll(map);
        return r1;
    }

    public static R1 ok() {
        return new R1();
    }

    public R1 put(String key, Object value) {
        super.put(key, value);
        return this;
    }
    public  Integer getCode() {

        return (Integer) this.get("code");
    }

}
