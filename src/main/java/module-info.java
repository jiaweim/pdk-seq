module pdk.seq {

    requires pdk.util;

    // external libraries
    requires org.jetbrains.annotations;
    requires com.google.common;
    requires it.unimi.dsi.fastutil;

    // export
    exports pdk.seq;
    exports pdk.seq.io;
}