

rem set JAVA_HOME=C:\Tools\graalvm-ce-java8-21.1.0
rem set PATH=%JAVA_HOME%\bin;%PATH%
set APPDIR=c:\Users\ssydoruk\GCTI
set APPDIR="C:\Users\ssydoruk\IdeaProjects\install"
rem set DBG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y
rem set DBG=

set JAVA_OPTS=-Dlog4j.configurationFile=%APPDIR%\etc\csUtils.xml -DlogPath=%APPDIR%\tmp -Dlog4j2.saveDirectory -Xms8000m -Xmx8000m %DBG%

%APPDIR%\bin\csUtils.bat -gui-profile=%APPDIR%\var\confUtil.txt
 
 