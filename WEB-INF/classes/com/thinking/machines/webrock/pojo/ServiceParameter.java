package com.thinking.machines.webrock.pojo;
public class ServiceParameter
{
private String dataType;
private String requestParameterName;
public ServiceParameter(String dataType,String requestParameterName)
{
this.dataType=dataType;
this.requestParameterName=requestParameterName;
}
public void setDataType(String dataType)
{
this.dataType=dataType;
}
public String getDataType()
{
return this.dataType;
}
public void setRequestParameterName(String requestParameterName)
{
this.requestParameterName=requestParameterName;
}
public String getRequestParameterName()
{
return this.requestParameterName;
}
}