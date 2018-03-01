tell application "Safari"
    set theURL to URL of current tab of window 1
tell application "System Events"
	set frontApp to name of first application process whose frontmost is true

	set output to frontApp & "|" & theURL
end tell
end tell 
