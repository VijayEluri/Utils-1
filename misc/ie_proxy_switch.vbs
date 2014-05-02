'Determine current proxy setting and toggle to oppisite setting

Option Explicit 

Dim WSHShell, strSetting
Set WSHShell = WScript.CreateObject("WScript.Shell")

strSetting = wshshell.regread("HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings\ProxyEnable")
If strSetting = 1 Then 
	WSHShell.regwrite "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings\ProxyEnable", 0, "REG_DWORD"
	WSHShell.Popup "IE Proxy OFF", 1
Else 
	WSHShell.regwrite "HKCU\Software\Microsoft\Windows\CurrentVersion\Internet Settings\ProxyEnable", 1, "REG_DWORD"
	WSHShell.Popup "IE Proxy ON", 1
End If