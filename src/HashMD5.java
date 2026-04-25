import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public class HashMD5 implements HashInterface
{
    // Specifies the per-round shift amounts
    private static final int[] s = {
            7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,
            5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,
            4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,
            6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21,
    };

    // Use binary integer part of the sines of integers (Radians) as constants:
    private static final int[] K = {
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
            0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
            0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
            0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
            0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
            0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
            0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
            0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
            0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
            0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
            0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
            0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
            0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
            0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
            0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
            0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391,
    };

    private int a0 = 0x67452301;
    private int b0 = 0xefcdab89;
    private int c0 = 0x98badcfe;
    private int d0 = 0x10325476;

    private void processChunk(IntBuffer M)
    {
        int A = a0, B = b0, C = c0, D = d0;
        for (int i = 0; i < 64; ++i)
        {
            int F, g;
            if (i < 16)
            {
                F = (B & C) | (~B & D);
                g = i;
            }
            else if (i < 32)
            {
                F = (D & B) | (~D & C);
                g = (5 * i + 1) % 16;
            }
            else if (i < 48)  {
                F = B ^ C ^ D;
                g = (3 * i + 5) % 16;
            }
            else
            {
                F = C ^ (B | ~D);
                g = (7 * i) % 16;
            }

            F = F + A + K[i] + M.get(g);
            A = D;
            D = C;
            C = B;
            B = B + Integer.rotateLeft(F, s[i]);
        }
        a0 += A;
        b0 += B;
        c0 += C;
        d0 += D;
    }

    // https://en.wikipedia.org/wiki/MD5#Pseudocode
    public String calcHash(InputStream is) throws IOException
    {
        byte[] buffer = new byte[512];
        IntBuffer chunkView = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
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
            System.arraycopy(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(originalLength * Byte.SIZE).array(), 0, buffer, buffer.length - Long.BYTES, Long.BYTES);

            // Process the final chunk
            processChunk(chunkView);
            break;
        }


        ByteBuffer digest = ByteBuffer.allocate(Integer.BYTES * 4).order(ByteOrder.LITTLE_ENDIAN).putInt(a0).putInt(b0).putInt(c0).putInt(d0);
        return HexFormat.of().formatHex(digest.array());
    }
}
