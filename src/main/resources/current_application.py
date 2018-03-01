#!/usr/bin/python

from AppKit import NSWorkspace
import time
import sys

#sleep_time=float(sys.argv[1])


activeAppName = NSWorkspace.sharedWorkspace().frontmostApplication()
print activeAppName

#runningApps =   NSWorkspace.sharedWorkspace().runningApplications()
#print runningApps