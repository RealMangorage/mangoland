let x = 10
mainloop: while x != 5 do
    print "Doing! " .. x
    while do
        print "Inner loop! " .. x
        x = x - 1
        if x == 7 then
            break mainloop
        end
    end
end

print "Done! " .. x