/*******************************************************************************
 *     Copyright 2009 Michael Elman (http://tarlogonjava.blogspot.com)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *     
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
package tarlog.encoder.tool.encoders;

import java.util.zip.Inflater;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.ui.swt.ddialog.api.fields.InputField;

public class InflaterEncoder extends AbstractEncoder {

    @InputField(name = "GZIP compatible compression")
    private boolean nowrap = true;

    @Override
    public Object encode(byte[] source) {
        try {
            Inflater decompresser = new Inflater(nowrap);
            decompresser.setInput(source, 0, source.length);
            byte[] tmpresult = new byte[4096];
            int resultLength = decompresser.inflate(tmpresult);
            decompresser.end();
            byte[] result = new byte[resultLength];
            System.arraycopy(tmpresult, 0, result, 0, resultLength);
            return result;
        } catch (Exception e) {
            showException(e);
            return null;
        }
    }

}
