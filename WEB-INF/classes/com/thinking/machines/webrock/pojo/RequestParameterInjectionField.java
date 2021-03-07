package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public class RequestParameterInjectionField
{
String parameterName;
Field field;
public RequestParameterInjectionField(String parameterName,Field field)
{
this.parameterName=parameterName;
this.field=field;
}
public void setParameterName(String parameterName)
{
this.parameterName=parameterName;
}
public String getParameterName()
{
return this.parameterName;
}
public void setField(Field field)
{
this.field=field;
}
public Field getField()
{
return this.field;
}
}