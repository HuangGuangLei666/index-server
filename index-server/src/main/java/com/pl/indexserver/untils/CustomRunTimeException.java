package com.pl.indexserver.untils;


public class CustomRunTimeException extends RuntimeException {


    private String errorCode;
    private String errorMsg;
    private String errorDetail;
    private Exception exception;
    private Object result;

    public CustomRunTimeException() {

    }
    /** service层调用 */
    public CustomRunTimeException(String errorCode) {
        this.errorCode = errorCode;
    }

    /** action层调用 */
    public CustomRunTimeException(String errorCode, String errorMsg) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public CustomRunTimeException(String errorCode, String errorMsg, String errorDetail) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorDetail = errorDetail;
    }

    public CustomRunTimeException(String errorCode, Object result) {
        super();
        this.errorCode = errorCode;
        this.result = result;
        printMessage(result);
    }

    public CustomRunTimeException(String errorCode, String errorMsg, Exception exception) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.exception = exception;
        printMessage(exception);
    }

    public CustomRunTimeException(String errorCode, String errorMsg, Object result) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.result = result;
        printMessage(result);
    }

    public CustomRunTimeException(String errorCode, String errorMsg, Object result, String errorDetail) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorDetail = errorDetail;
        this.result = result;
        printMessage(result);
    }

    private void printMessage(Object result) {
//        if (null != result) {
//            if (result instanceof exception)
//                logger.(String.format("exception【%s】", getStackMsg((exception) result)));
//            else
//                logger.error(String.format("result【%s】", result.toString()));
//        }
    }

    private String getStackMsg(Exception e) {
        StringBuffer sb = new StringBuffer();
        sb.append(e.toString()).append("\n");
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }

    private String getStackMsg(Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(e.toString()).append("\n");
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public Throwable fillInStackTrace() {

        return super.fillInStackTrace();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}