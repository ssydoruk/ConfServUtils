package com.ssydoruk.confservutils;

import org.immutables.value.Value;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


@Value.Immutable(singleton = true, builder = false)
@Main.MySingleton

public interface ICSVFileChooser {
    @Value.Lazy
    default JFileChooser dlg() {
        return  new JFileChooser();
    }
}
