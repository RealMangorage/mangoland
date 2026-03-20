let x 1

function zero
    printstr "x is zero"
    return
end

function notzero
    printstr "x is not zero"
    return
end

function check_x
    load x
    push 0
    equals

    jump_if_true zero notzero
    return
end

call check_x

push 0
store x

call check_x
