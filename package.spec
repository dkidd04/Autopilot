%define __os_install_post /usr/lib/rpm/brp-compress 
%{nil}

Summary: Liquifi Autopilot
Name: LiqFiAuto
Version: 1.9
Release: 1
Group: Development/Libraries
License: Commercial
####################################################################
# This line allows you to specify a directory as the ``root'' 
# for building and installing the new package. 
# You can use this to help test your package before having it installed on your machine
####################################################################
BuildRoot: %{_builddir}/
#Prefix: /opt

%description
Liquifi Autopilot business logic regression test process.

# The %prep, %build and %install macros are unused in this project
%prep
%build
%install

####################################################################################
# Simply list all the files that need to be installed by this RPM
# Package will be installed as per the following dir structure
####################################################################################
%files
%defattr(755,@func_user@,@func_group@)
/opt/@func_group@/LiqFiAuto/%{version}


###################################################################################
# %pre and %post are install scripts that run before and after package installation
# respectively. %preun and %postun are uninstall scripts that run before and after
# package deinstallation respectively. To maintain control over the files installed
# by the package, any files that would be created by an install script should have
# an empty file listed in the file list. The empty file would then be overwritten
# by the postinstall script. If you do this, remember to set the verification 
# requirements for the dummy file to only check its existence.
###################################################################################

%pre
echo preinstall
#rm -r  /opt/@func_user@/%{name}/%{version}/
#rm -r  /opt/@func_user@/%{name}/%{version}/bin/
#rm -r /opt/@func_user@/%{name}/%{version}/lib/
#rm -r /opt/@func_user@/%{name}/%{version}/config/

%post
echo postinstall
cd /opt/@func_group@/%{name}/

#Here we are checking to see if we are installing on a prod box to decide if we create a symbolic link to this version.
installHost=`hostname`

if  [[ "${installHost}" == eqliqap*p ]]; then
	echo "not setting the currentVersion symbolic link as this IS a prod box."
else
	echo "setting the currentVersion symbolic link as this is NOT a prod box."
	rm currentVersion
	ln -s %{version} currentVersion
	chown -h @func_user@:@func_group@ currentVersion
fi

%preun
echo preuninstall

%postun
echo postuninstall

# Following will be done as part of uninstallation
# All dir as specified under %file will already be 
# deleted before uninstall is called
%uninstall
echo uninstalling

