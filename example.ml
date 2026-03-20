let x 0

if (x == 0) do
    printstr "X is zero"
else
    printstr "X is not zero"
end

printstr "Hello, World!"

    jump_if_true zero notzero
    return
end

call check_x

push 0
store x

call check_x
