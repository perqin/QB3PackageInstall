# QB3PackageInstall

The sandbox and storage permissions framework is broken on Android Q Beta 3. When an app is allowed to install unknown apk, it is also forced to use Scoped Storage (storage sandbox, introduced in Q). This break file manager apps, because once we grant the permission and allow then to install apk file, it can't access external storage anymore.

In this case, you can share the apk file in file manager to this app (QB3 Package Install), and it will copy the apk file and request installation, leaving the file manager's REQUEST_INSTALL_PACKAGES permission denied.
