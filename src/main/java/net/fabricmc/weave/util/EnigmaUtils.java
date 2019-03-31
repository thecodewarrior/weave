/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.weave.util;

import cuchaz.enigma.translation.representation.MethodDescriptor;
import cuchaz.enigma.translation.representation.TypeDescriptor;
import cuchaz.enigma.translation.representation.entry.*;
import net.fabricmc.weave.CommandFindMappingErrors;
import net.fabricmc.weave.CommandTinyify;
import net.fabricmc.weave.Main;

public class EnigmaUtils {
    public static String[] serializeEntry(Entry<?> entry, boolean removeNone, String... extraFields) {
        String[] data = null;

        if (entry instanceof FieldEntry) {
            data = new String[4 + extraFields.length];
            data[0] = "FIELD";
            data[1] = entry.getContainingClass().getFullName();
            data[2] = ((FieldEntry) entry).getDesc().toString();
            data[3] = entry.getName();

            if (removeNone) {
                data[1] = Utils.NONE_PREFIX_REMOVER.map(data[1]);
                data[2] = Utils.NONE_PREFIX_REMOVER.mapDesc(data[2]);
            }
        } else if (entry instanceof MethodEntry) {
            data = new String[4 + extraFields.length];
            data[0] = "METHOD";
            data[1] = entry.getContainingClass().getFullName();
            data[2] = ((MethodEntry) entry).getDesc().toString();
            data[3] = entry.getName();

            if (removeNone) {
                data[1] = Utils.NONE_PREFIX_REMOVER.map(data[1]);
                data[2] = Utils.NONE_PREFIX_REMOVER.mapMethodDesc(data[2]);
            }
        } else if (entry instanceof LocalVariableEntry) {
            MethodEntry method = ((LocalVariableEntry) entry).getParent();
            data = new String[5 + extraFields.length];
            data[0] = "VARIABLE";
            data[1] = method.getContainingClass().getFullName();
            data[2] = method.getDesc().toString();
            data[3] = method.getName();
            data[4] = "" + ((LocalVariableEntry) entry).getIndex();

            if (removeNone) {
                data[1] = Utils.NONE_PREFIX_REMOVER.map(data[1]);
                data[2] = Utils.NONE_PREFIX_REMOVER.mapMethodDesc(data[2]);
            }
        } else if (entry instanceof ClassEntry) {
            data = new String[2 + extraFields.length];
            data[0] = "CLASS";
            data[1] = ((ClassEntry) entry).getFullName();

            if (removeNone) {
                data[1] = Utils.NONE_PREFIX_REMOVER.map(data[1]);
            }
        }

        if (data != null) {
            System.arraycopy(extraFields, 0, data, data.length - extraFields.length, extraFields.length);
        }

        return data;
    }

    public static Entry<?> deserializeEntry(String[] data) {
        if (data.length > 0) {
            if (data[0].equals("FIELD") && data.length >= 4) {
                return new FieldEntry(new ClassEntry(data[1]), data[3], new TypeDescriptor(data[2]));
            } else if (data[0].equals("METHOD") && data.length >= 4) {
                return new MethodEntry(new ClassEntry(data[1]), data[3], new MethodDescriptor(data[2]));
            } else if (data[0].equals("CLASS") && data.length >= 2) {
                return new ClassEntry(data[1]);
            }
        }

        return null;
    }

    public static void addEnigmaCommands() {
        Main.addCommand(new CommandTinyify());
        Main.addCommand(new CommandFindMappingErrors());
    }
}
