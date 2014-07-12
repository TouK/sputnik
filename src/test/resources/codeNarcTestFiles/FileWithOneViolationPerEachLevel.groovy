class FileWithOneViolationPerEachLevel {
    int myMethod(int count) {
        try {
            int i = 0;
            for (; i < 5;) {     // Violation L1
                println i++
            }
        } finally {
            assert count > 0        // violation L2
        }
    }

    String bar() {} //violation L3

}
