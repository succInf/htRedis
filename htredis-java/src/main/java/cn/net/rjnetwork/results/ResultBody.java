package cn.net.rjnetwork.results;

import lombok.Data;

@Data
public class ResultBody {

    private String code;

    private String msg;

    private Object data;

    public static ResultBody success(String msg, Object data) {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode("000000");
        resultBody.setData(data);
        resultBody.setMsg(msg);
        return resultBody;
    }

    public static ResultBody success(String msg) {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode("000000");
        resultBody.setMsg(msg);
        return resultBody;
    }


    public static ResultBody success(Object data) {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode("000000");
        resultBody.setMsg("");
        resultBody.setData(data);
        return resultBody;
    }
    public static ResultBody success() {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode("000000");
        resultBody.setMsg("success");
        return resultBody;
    }



    public static ResultBody error(String msg) {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode("000001");
        resultBody.setMsg(msg);
        return resultBody;
    }

    public static ResultBody error(String code,String msg) {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode(code);
        resultBody.setMsg(msg);
        return resultBody;
    }

    public static ResultBody error() {
        ResultBody resultBody = new ResultBody();
        resultBody.setCode("000001");
        resultBody.setMsg("error");
        return resultBody;
    }

}
