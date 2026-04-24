import java.io.IOException;
import java.io.InputStream;

public class HashPiki implements HashInterface
{
    // Based on `String::calcHash` method from Pikmin 1 which I decompiled.
    // https://github.com/projectPiki/pikmin/blob/main/src/sysCommon/string.cpp
    public String calcHash(InputStream is) throws IOException
    {
        int hash = 0;
        for (int b; (b = is.read()) != -1;)
        {
            hash = (hash << 4) + b;
            int highNibble = hash & 0xf0000000;
            if (highNibble != 0)
                hash ^= highNibble >>> 24;
            hash &= ~highNibble;
        }
        return String.format("%08X", hash);
    }
}