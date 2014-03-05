echo off
@set LOCALCLASSPATH=./target/classes/
@for %%i in (".\WebContent\WEB-INF\lib\*.jar") do call "setpath.bat" %%i


set CLASSPATH=%LOCALCLASSPATH%;%CLASSPATH%
echo on
#java -Dxport="%1"  -Xmx512m com.jetty.MyServer %2 %3 %4
java -Xms512m -Xmx512m -Dxport=8080 -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8787   org.sbs.jetty.StartServer