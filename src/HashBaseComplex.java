import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public abstract class HashBaseComplex implements HashInterface
{
    protected ByteOrder mByteOrder;

    HashBaseComplex(ByteOrder byteOrder)
    {
        mByteOrder = byteOrder;
    }

    abstract protected void processChunk(IntBuffer chunk);

    @Override
    public void calcHash(InputStream is) throws IOException
    {
        byte[] buffer = new byte[512 / Byte.SIZE];
        IntBuffer chunkView = ByteBuffer.wrap(buffer).order(mByteOrder).asIntBuffer();
        long originalLength = 0;

        while (true)
        {
            int readSize = is.readNBytes(buffer, 0, buffer.length);
            originalLength += readSize;

            // Another chunk bytes the dust.
            if (readSize == buffer.length)
            {
                processChunk(chunkView);
                continue;
            }

            // End of stream reached.  Append 1 to end of bitstream.
            buffer[readSize++] = (byte)0x80;

            // Handle edge case wherein there is not enough space to write the original length bytes.
            if (readSize >= buffer.length - Long.BYTES)
            {
                for (int i = readSize; i < buffer.length; ++i)
                    buffer[i] = 0;
                processChunk(chunkView);
                readSize = 0;
            }

            // Fill the rest of the buffer with zeroes followed by the 64-bit original size (in BITS).
            for (int i = readSize; i < buffer.length - Long.BYTES; ++i)
                buffer[i] = 0;
            System.arraycopy(ByteBuffer.allocate(Long.BYTES).order(mByteOrder).putLong(originalLength * Byte.SIZE).array(), 0, buffer, buffer.length - Long.BYTES, Long.BYTES);

            // Process the final chunk
            processChunk(chunkView);
            break;
        }
    }
}
