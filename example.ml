function check(x, y, z)
    print "x: " .. x
    print "y: " .. y
    print "z: " .. z
    return z
end

let result = call check(1, 2, 3)
result = result * 100 / 2 * 10000
print "Result: " .. result
