import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class HashSHA1 extends HashBaseComplex
{
    private int h0 = 0x67452301;
    private int h1 = 0xEFCDAB89;
    private int h2 = 0x98BADCFE;
    private int h3 = 0x10325476;
    private int h4 = 0xC3D2E1F0;

    public HashSHA1()
    {
        super(ByteOrder.BIG_ENDIAN);
    }

    protected void processChunk(IntBuffer M)
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

    public String toString()
    {
        return String.format("%08x%08x%08x%08x%08x", h0, h1, h2, h3, h4);
    }
}
