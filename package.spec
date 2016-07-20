Summary: Liquifi Autopilot
Name: _NAME_
Version: _VERSION_
Release: _RELEASE_
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
Liquifi Autopilot business logic regression test process. %{version} (build %{release})

%define __jar_repack 0
%global _binary_filedigest_algorithm 1
%global _source_filedigest_algorithm 1
%define _source_payload w0.gzdio
%define _binary_payload w0.gzdio

%prep
/bin/mkdir %{_buildrootdir}/%{name}-%{version}-%{release}.%{_arch}
%build
%install
/bin/cp -r %{_builddir}/* %{_buildrootdir}/%{name}-%{version}-%{release}.%{_arch}/

####################################################################################
# Simply list all the files that need to be installed by this RPM
# Package will be installed as per the following dir structure
####################################################################################
%files
%defattr(755,@func_user@,@func_group@)
/opt/liquifi/%{name}/%{version}

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

%preun
echo preuninstall

%postun
echo postuninstall

# Following will be done as part of uninstallation
# All dir as specified under %file will already be 
# deleted before uninstall is called
%uninstall
echo uninstalling