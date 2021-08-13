package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IMemoryBlock;

import java.nio.ByteBuffer;


/**
 * Wrapper to a block of memory implemented as a byte array.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MemoryBlockImpl implements IMemoryBlock
{
    private final ByteBuffer buffer;


    /**
     * Constructor.
     *
     * @param size The size of the memory block
     */
    public MemoryBlockImpl (final int size)
    {
        this.buffer = ByteBuffer.allocateDirect (size);
    }


    /** {@inheritDoc} */
    @Override
    public ByteBuffer createByteBuffer ()
    {
        this.buffer.rewind ();
        return this.buffer;
    }
}
