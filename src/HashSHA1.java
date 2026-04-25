import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class HashSHA1 implements HashInterface
{
    private int h0 = 0x67452301;
    private int h1 = 0xEFCDAB89;
    private int h2 = 0x98BADCFE;
    private int h3 = 0x10325476;
    private int h4 = 0xC3D2E1F0;

    private void processChunk(IntBuffer M)
    {
        int[] W = new int[80];
        for (int i = 0; i < 16; ++i)
            W[i] = M.get(i);
        for (int i = 16; i < W.length; ++i)
            W[i] = Integer.rotateLeft(W[i-3] ^ W[i-8] ^ W[i-14] ^ W[i-16], 1);

        int a = h0;
        int b = h1;
        int c = h2;
        int d = h3;
        int e = h4;

        for (int i = 0; i < W.length; ++i)
        {
            int f, k;
            if (i < 20)
            {
                f = (b & c) | (~b & d);
                k = 0x5A827999;
            }
            else if (i < 40)
            {
                f = b ^ c ^ d;
                k = 0x6ED9EBA1;
            }
            else if (i < 60)
            {
                f = (b & c) | (b & d) | (c & d);
                k = 0x8F1BBCDC;
            }
            else
            {
                f = b ^ c ^ d;
                k = 0xCA62C1D6;
            }

            int temp = Integer.rotateLeft(a, 5) + f + e + k + W[i];
            e = d;
            d = c;
            c = Integer.rotateLeft(b, 30);
            b = a;
            a = temp;
        }

        h0 += a;
        h1 += b;
        h2 += c;
        h3 += d;
        h4 += e;
    }

    public String calcHash(InputStream is) throws IOException
    {
        byte[] buffer = new byte[512 / Byte.SIZE];
        IntBuffer chunkView = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
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
            buffer[readSize++] = (byte) 0x80;

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
            System.arraycopy(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(originalLength * Byte.SIZE).array(), 0, buffer, buffer.length - Long.BYTES, Long.BYTES);

            // Process the final chunk
            processChunk(chunkView);
            break;
        }
        return String.format("%08X%08X%08X%08X%08X", h0, h1, h2, h3, h4);
    }
}
