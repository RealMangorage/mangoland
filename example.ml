let x = 12
while do
    print "Hello, world!"
    load x
    print
    if (x == 0) then
        break
    end
    load x
    decrement
    store x
end

print "Goodbye, world!"

function test()
    print "This is a test function."
    load x
    print
    return
end

call test()

print "End of program."