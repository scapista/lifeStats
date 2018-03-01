tell application "Safari"

    --Variables
    set windowCount to number of windows
    set docText to ""

    --Repeat for Every Window
    repeat with x from 1 to windowCount
        set tabcount to number of tabs in window x

		--Repeat for Every Tab in Current Window
        repeat with y from 1 to tabcount

            --Get Tab Name & URL
            set tabName to name of tab y of window x
            set tabURL to URL of tab y of window x
			
			set docText to docText & x & "|" & tabURL & "|" & tabName & linefeed as string
    end repeat

    end repeat
end tell

