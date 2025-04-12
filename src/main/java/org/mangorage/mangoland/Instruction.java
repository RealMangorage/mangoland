package org.mangorage.mangoland;

import org.mangorage.mangoland.persistance.Persistence;

/**

     INST    INST
    HEADER   DATA
    [1234]  [....]
 */
public interface Instruction {
    void process(final byte[] instruction, final Persistence persistence);

    byte[] compile(String code);
}
