package com.amnoon.protocal;

public enum Field {

    // username
    USERNAME((char)0x01),
    
    // password
    PASSWORD((char)0x02),
    
    // whether it is success
    SUCCESS((char)0x03),

    // unknown, appears while login successfully
    UNKNOWN05((char)0x05),
    UNKNOWN06((char)0x06),

    // mac address
    MAC((char)0x07),
    
    // session (NOTE: wrong in return packet)
    SESSION((char)0x08),
    
    // ip address
    IP((char)0x09),
    
    // access point
    ENTRY((char)0x0A),
    
    // message (NOTE: wrong in return packet)
    MESSAGE((char)0x0B),
    
    // server ip address
    SERVER((char)0x0C),

    // unknown, appears while received server ip address
    UNKNOWN0D((char)0x0D),

    // is dhcp enabled
    DHCP((char)0x0E),
    
    // self-services website link
    WEBSITE((char)0x13),
    
    // serial no
    INDEX((char)0x14),
    
    // version
    VERSION((char)0x1F),

    // unknown, appears while login successfully
    UNKNOWN20((char)0x20),
    UNKNOWN23((char)0x23),

    // disconnect reason
    REASON((char)0x24),
    
    // 4 bytes blocks, send in breathe and logout
    BLOCK2A((char)0x2A),
    BLOCK2B((char)0x2B),
    BLOCK2C((char)0x2C),
    BLOCK2D((char)0x2D),
    BLOCK2E((char)0x2E),
    BLOCK2F((char)0x2F),
    
    // unknown 4 bytes blocks, appears while confirmed
    BLOCK30((char)0x30),
    BLOCK31((char)0x31),
    
    // unknown
    UNKNOWN32((char)0x32),
    
    // 4 bytes blocks, appears while login successfully
    BLOCK34((char)0x34),
    BLOCK35((char)0x35),
    BLOCK36((char)0x36),
    BLOCK37((char)0x37),
    BLOCK38((char)0x38);

    Field(char value) {}

}
