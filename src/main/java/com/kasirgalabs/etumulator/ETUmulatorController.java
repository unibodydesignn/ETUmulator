/*
 * Copyright (C) 2017 Kasirgalabs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kasirgalabs.etumulator;

import com.kasirgalabs.etumulator.console.Console;
import com.kasirgalabs.etumulator.document.Document;
import com.kasirgalabs.etumulator.pattern.Registry;
import com.kasirgalabs.etumulator.processor.Processor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ETUmulatorController implements Initializable, Console {
    @FXML
    private Button runButton;
    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                textArea.appendText(String.valueOf((char) b));
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    @Override
    public void write(Object o) {
    }

    @FXML
    private void runButtonOnAction(ActionEvent event) {
        Processor processor = Registry.get(Processor.class);
        String text = Registry.get(Document.class).getText();
        char[] code = (text + "\n").toCharArray();
        processor.run(code);
    }
}
