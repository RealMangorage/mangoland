function check(x, y, z)
    print "x: " .. x
    print "y: " .. y
    print "z: " .. z
    if (z != x) then
        print "z is 3, incrementing z"
        z = z + 1
    end
    return z
end

let result = call check(1, 2, 3)
result = result * 100 / 2 * 10000
print "Result: " .. result
