# ============================================================
# MangoLang full feature showcase / stress demo
#
# This file is intentionally long and tries to exercise nearly
# every currently supported source-level feature and instruction.
#
# Notes:
# - `print value` is not supported directly, so stack values are
#   printed via `load value` + `print`, or via concatenation.
# - `exit` is included in an unreachable branch so the file stays
#   runnable during automated testing.
# ============================================================

print "=== MangoLang Showcase Start ==="

# ------------------------------------------------------------
# Section 1: literals, let bindings, load/print, and type
# ------------------------------------------------------------
let baseNumber = 10
let baseText = "mango"
let baseTruth = true

print "Base number: " .. baseNumber
print "Base text: " .. baseText
print "Base truth: " .. baseTruth

type baseNumber
store baseNumberType
print "Type of baseNumber: " .. baseNumberType

type baseText
store baseTextType
print "Type of baseText: " .. baseTextType

type baseTruth
store baseTruthType
print "Type of baseTruth: " .. baseTruthType

load baseNumber
print

# ------------------------------------------------------------
# Section 2: direct stack instructions: push, dup, add, store
# ------------------------------------------------------------
push 21
dup
add
store duplicatedTotal
print "Duplicated total: " .. duplicatedTotal

push "stacked"
store stackedWord
print "Stored stacked word: " .. stackedWord

# ------------------------------------------------------------
# Section 3: raw arithmetic/comparison instructions
# ------------------------------------------------------------
load duplicatedTotal
push 2
divide
store dividedTotal
print "Divided total: " .. dividedTotal

load dividedTotal
push 3
multiply
store multipliedTotal
print "Multiplied total: " .. multipliedTotal

load multipliedTotal
push 4
subtract
store subtractedTotal
print "Subtracted total: " .. subtractedTotal

push 4
decrement
store decrementedValue
print "Decremented value: " .. decrementedValue

push 5
push 5
equals
store rawEqualsResult
print "Raw equals result: " .. rawEqualsResult

push 3
greater_then_zero
store positiveCheck
print "Positive check: " .. positiveCheck

push -1
greater_then_zero
store negativeCheck
print "Negative check: " .. negativeCheck

# ------------------------------------------------------------
# Section 4: expression-based arithmetic and reassignment
# ------------------------------------------------------------
let difference = 20 - 5
let product = 6 * 7
let quotient = 20 / 4
let combined = difference + quotient

print "Difference: " .. difference
print "Product: " .. product
print "Quotient: " .. quotient
print "Combined: " .. combined

combined = combined + 100
print "Combined after reassignment: " .. combined

# ------------------------------------------------------------
# Section 5: if conditions and variable-to-variable comparisons
# ------------------------------------------------------------
let mirrorValue = 15

if (difference == 15) then
    print "Difference literal comparison passed"
end

if (difference == mirrorValue) then
    print "Variable-to-variable comparison passed"
end

if (difference != product) then
    print "Difference and product are different"
end

if (baseText != "banana") then
    print "String inequality passed"
end

if (baseTruth == true) then
    print "Boolean equality passed"
end

# ------------------------------------------------------------
# Section 6: while loops, inline conditions, and break
# ------------------------------------------------------------
let countdown = 3
while countdown != 0 do
    print "Countdown: " .. countdown
    countdown = countdown - 1
end

let outer = 4
mainloop: while outer != 0 do
    print "Outer loop: " .. outer
    while do
        print "Inner loop sees: " .. outer
        outer = outer - 1
        if outer == 1 then
            break mainloop
        end
    end
end
print "After labeled break: " .. outer

# ------------------------------------------------------------
# Section 7: functions, parameters, calls, returns
# ------------------------------------------------------------
function banner()
    print "Inside banner()"
end

function describeTriple(a, b, c)
    print "Triple A: " .. a
    print "Triple B: " .. b
    print "Triple C: " .. c
end

function answer()
    return 42
end

function addTwo(left, right)
    let sum = left + right
    return sum
end

function bumpIfThree(value)
    if (value == 3) then
        value = value + 1
    end
    return value
end

call banner()
call describeTriple baseNumber 123 456

let answerValue = call answer()
print "Answer value: " .. answerValue

let sumValue = call addTwo(7, 8)
print "Sum value: " .. sumValue

let bumpedValue = call bumpIfThree(3)
print "Bumped value: " .. bumpedValue

call answer()
print

# ------------------------------------------------------------
# Section 8: type checks on derived values
# ------------------------------------------------------------
type answerValue
store answerType
print "Type of answerValue: " .. answerType

type rawEqualsResult
store equalsType
print "Type of rawEqualsResult: " .. equalsType

# ------------------------------------------------------------
# Section 9: long filler section to stress compiler/VM length
# ------------------------------------------------------------
let filler00 = 0
let filler01 = 1
let filler02 = 2
let filler03 = 3
let filler04 = 4
let filler05 = 5
let filler06 = 6
let filler07 = 7
let filler08 = 8
let filler09 = 9
let filler10 = 10
let filler11 = 11
let filler12 = 12
let filler13 = 13
let filler14 = 14
let filler15 = 15
let filler16 = 16
let filler17 = 17
let filler18 = 18
let filler19 = 19
let filler20 = 20
let filler21 = 21
let filler22 = 22
let filler23 = 23
let filler24 = 24
let filler25 = 25
let filler26 = 26
let filler27 = 27
let filler28 = 28
let filler29 = 29
let filler30 = 30
let filler31 = 31
let filler32 = 32
let filler33 = 33
let filler34 = 34
let filler35 = 35
let filler36 = 36
let filler37 = 37
let filler38 = 38
let filler39 = 39

print "Filler checkpoint A: " .. filler00
print "Filler checkpoint B: " .. filler20
print "Filler checkpoint C: " .. filler39

# ------------------------------------------------------------
# Section 10: late control-flow after the long prefix
# ------------------------------------------------------------
function lateFunctionDemo()
    print "Late function demo reached"
end

let tailCounter = 2
while tailCounter != 0 do
    print "Tail counter: " .. tailCounter
    tailCounter = tailCounter - 1
    if (tailCounter == 1) then
        print "Calling lateFunctionDemo from loop"
    else
        print "Not calling lateFunctionDemo yet"
    end
end

call lateFunctionDemo()

# ------------------------------------------------------------
# Section 11: sleep, halt, and optional exit coverage
# ------------------------------------------------------------
print "Sleeping briefly..."
push 10
sleep

if (0 == 1) then
    print "Unreachable exit branch"
    exit
else
    print "Reached else branch, skipping exit"
end

print "=== MangoLang Showcase Complete ==="
halt
