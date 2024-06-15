@rem ******************************************************************************
@rem ** Gradle startup script for Windows
@rem ** For details, see http://www.gradle.org/
@rem ******************************************************************************

@if "%DEBUG%" == "" @echo off
@setlocal

@rem Determine the bitness of the JVM used to launch this script
set SCRIPT_DIR=%~dp0
set APP_HOME=%SCRIPT_DIR%

set DEFAULT_JVM_OPTS=
set GRADLE_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME:~-1%"=="\" set DIRNAME=%DIRNAME:~0,-1%
set APP_HOME=%DIRNAME%

@rem Find Java
if "%JAVA_HOME%"=="" set JAVA_EXE=java.exe
if not "%JAVA_HOME%"=="" set JAVA_EXE="%JAVA_HOME%/bin/java.exe"

@rem Setup the command line
set CLASSPATH=%APP_HOME%/gradle/wrapper/gradle-wrapper.jar

set CMD_LINE_ARGS=
set firstArg=%1
if not "%firstArg%"=="" set CMD_LINE_ARGS=%CMD_LINE_ARGS% "%firstArg%"
shift
:buildCmdLine
set firstArg=%1
if not "%firstArg%"=="" set CMD_LINE_ARGS=%CMD_LINE_ARGS% "%firstArg%"
shift
if not "%firstArg%"=="" goto buildCmdLine

set CMD=%JAVA_EXE% %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%

@rem Execute Gradle
%CMD%
endlocal
