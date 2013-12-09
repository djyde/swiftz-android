package com.amnoon.proto;

public enum Field {

    // username
    USERNAME((byte)0x01),
    
    // password
    PASSWORD((byte)0x02),
    
    // whether it is success
    SUCCESS((byte)0x03),

    // unknown, appears while login successfully
    UNKNOWN05((byte)0x05),
    UNKNOWN06((byte)0x06),

    // mac address
    MAC((byte)0x07),
    
    // session (NOTE: wrong in return packet)
    SESSION((byte)0x08),
    
    // ip address
    IP((byte)0x09),
    
    // access point
    ENTRY((byte)0x0A),
    
    // message (NOTE: wrong in return packet)
    MESSAGE((byte)0x0B),
    
    // server ip address
    SERVER((byte)0x0C),

    // unknown, appears while received server ip address
    UNKNOWN0D((byte)0x0D),

    // is dhcp enabled
    DHCP((byte)0x0E),
    
    // self-services website link
    WEBSITE((byte)0x13),
    
    // serial no
    INDEX((byte)0x14),
    
    // version
    VERSION((byte)0x1F),

    // unknown, appears while login successfully
    UNKNOWN20((byte)0x20),
    UNKNOWN23((byte)0x23),

    // disconnect reason
    REASON((byte)0x24),
    
    // 4 bytes blocks, send in breathe and logout
    BLOCK2A((byte)0x2A),
    BLOCK2B((byte)0x2B),
    BLOCK2C((byte)0x2C),
    BLOCK2D((byte)0x2D),
    BLOCK2E((byte)0x2E),
    BLOCK2F((byte)0x2F),
    
    // unknown 4 bytes blocks, appears while confirmed
    BLOCK30((byte)0x30),
    BLOCK31((byte)0x31),
    
    // unknown
    UNKNOWN32((byte)0x32),
    
    // 4 bytes blocks, appears while login successfully
    BLOCK34((byte)0x34),
    BLOCK35((byte)0x35),
    BLOCK36((byte)0x36),
    BLOCK37((byte)0x37),
    BLOCK38((byte)0x38);

    Field(byte value) {}

    public static Field fromByte(byte field) {
        return null;
    }

    public byte value() {
        return 0;
    }

}
