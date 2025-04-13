package org.mangorage.mangoland.core.instruction;

import org.mangorage.mangoland.core.persistance.Persistence;

/**

     INST    INST
    HEADER   DATA
    [1234]  [....]
 */
public interface Instruction {
    void process(final byte[] instruction, final Persistence persistence);

    byte[] compile(String code);
}
