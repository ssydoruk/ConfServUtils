

rem set JAVA_HOME=C:\Tools\graalvm-ce-java8-21.1.0
rem set PATH=%JAVA_HOME%\bin;%PATH%
set APPDIR=c:\Users\ssydoruk\GCTI

set JAVA_OPTS="-Dlog4j.configurationFile=%APPDIR%\etc\csUtils.xml -Xms8000m -Xmx8000m"

%APPDIR%\lib\bin\csUtils.bat -gui-profile=%APPDIR%\var\confUtil.txt --log-file=%APPDIR%\tmp\csUtil 
 