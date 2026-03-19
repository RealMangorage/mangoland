let x 0

function whelp
    printstr "x is zero"
    return
end

function check_x
    load x
    push 0
    equals

    jump_if_false whelp
end

call check_x