// Based on `String::calcHash` method from Pikmin 1 which Bradley decompiled.
// https://github.com/projectPiki/pikmin/blob/main/src/sysCommon/string.cpp
public class HashPiki extends HashBaseSimple
{
    private int digest = 0;

    protected void processChunk(byte[] chunk, int length)
    {
        for (int i = 0; i < length; ++i)
        {
            digest = (digest << 4) + chunk[i];
            int highNibble = digest & 0xf0000000;
            if (highNibble != 0)
                digest ^= highNibble >>> 24;
            digest &= ~highNibble;
        }
    }

    public String toString()
    {
        return String.format("%08x", digest);
    }
}