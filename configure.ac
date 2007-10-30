AC_INIT(apoc-desktop-agent, 1.0)

AM_INIT_AUTOMAKE
AC_JAVA_OPTIONS

# Setting APOC Daemon dir
AC_EXPAND(libdir,NONE,EXPANDED_LIBDIR)

APOCD_DIR="$EXPANDED_LIBDIR/apoc"

# Checking for# Setting APOC class path
AC_EXPAND(datadir, NONE, EXPANDED_DATADIR)

APOCDIR=$EXPANDED_DATADIR'/lib/apoc'

AC_ARG_WITH(apoc-path,
AC_HELP_STRING( [--with-apoc-path=DIR],
		[specify a path to install the apoc libraries (DATADIR/lib/apoc)]),
[
	if test "$withval" != "no"
	then
		APOCDIR=$withval
	fi
])

SPIJDK=$APOCDIR/spi.jar

export CLASSPATH=$CLASPATH:$SPIJDK

AC_PROG_JAVAC
AC_PROG_JAVA
AC_PROG_JAR

AC_CHECK_CLASS(com.sun.apoc.spi.PolicyManager, [], [
echo
AC_MSG_ERROR(APOC SPI not found.
If it is already installed please use --with-apoc-path=DIR to point to the path wher the spi.jar file is stored.)
])

AC_CHECK_PROG(ANT, ant, ant, no)
if test $ANT == "no"
then
	AC_MSG_ERROR(Apache's Ant was not found in path.)
fi

AC_SUBST(APOCDIR)
AC_SUBST(SPIJDK)
AC_SUBST(LDAPJDK)

AC_OUTPUT([
Makefile
src/Makefile
src/profile-editor
src/com/Makefile
src/com/sun/Makefile
src/com/sun/apoc/Makefile
src/com/sun/apoc/tools/Makefile
src/com/sun/apoc/tools/profileeditor/Makefile
src/com/sun/apoc/tools/profileeditor/build.xml
])
