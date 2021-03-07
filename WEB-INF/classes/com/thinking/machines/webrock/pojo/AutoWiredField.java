package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public class AutoWiredField implements java.io.Serializable
{
private String name;
private Field field;
public AutoWiredField(String name,Field field)
{
this.name=name;
this.field=field;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
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