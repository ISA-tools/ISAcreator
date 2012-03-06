package org.isatools.isacreator.formatmappingutility.utils;

import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.model.GeneralFieldTypes;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/01/2012
 *         Time: 17:16
 */
public class TableReferenceObjectWrapper {

    private TableReferenceObject tableReferenceObject;

    private boolean constructProtocolsWithDefaultValues = false;

    public TableReferenceObjectWrapper(TableReferenceObject tableReferenceObject) {
        this.tableReferenceObject = tableReferenceObject;
    }

    public Vector<String> getStdHeaders() {

        List<FieldObject> fields = new ArrayList<FieldObject>();
        Vector<String> headers = new Vector<String>();
        headers.add(TableReferenceObject.ROW_NO_TEXT);

        if (tableReferenceObject.getTableFields() == null) {
            for (String key : tableReferenceObject.getFieldLookup().keySet()) {
                fields.add(tableReferenceObject.getFieldLookup().get(key));
            }
        } else {
            fields = tableReferenceObject.getTableFields().getFields();
        }

        tableReferenceObject.sortFieldsByColumnNumber(fields);

        for (FieldObject sortedField : fields) {
            if (!sortedField.isHidden()) {
                if (sortedField.getFieldName().equals(GeneralFieldTypes.PROTOCOL_REF.name)) {
                    headers.add(sortedField.getFieldName() + " (" + sortedField.getDefaultVal() + ")");
                } else {
                    headers.add(sortedField.getFieldName());
                }
            }
        }

        return headers;
    }

    public List<Protocol> findProtocols() {
        List<Protocol> protocols = new ArrayList<Protocol>();

        List<FieldObject> fieldsAsList = getFields();

        StringBuffer protocolParameters = new StringBuffer();
        FieldObject currentProtocol = null;
        int count = 0;
        int prevParameterIndex = -1;

        for (FieldObject field : fieldsAsList) {
            // build up string representation of protocol definitions, then process and add them to a protocol object
            // for addition into the protocols to add List!
            System.out.println(field.getFieldName() + " - " + field.getDefaultVal());
            if (field.getFieldName().equals(GeneralFieldTypes.PROTOCOL_REF.name)) {

                System.out.println("Encountered a Protocol REF, type is: " + field.getDefaultVal());
                if (currentProtocol != null) {
                    // then we have a protocol directly after another protocol
                    constructProtocols(protocols, currentProtocol, protocolParameters.toString());
                }

                currentProtocol = field;
                protocolParameters = new StringBuffer();

            } else if (field.getFieldName().contains(GeneralFieldTypes.PARAMETER_VALUE.name)) {
                String parameter = field.getFieldName().substring(field.getFieldName().indexOf("[") + 1, field.getFieldName().indexOf("]"));
                protocolParameters.append(parameter).append(";");
                prevParameterIndex = count;
            } else if (field.getFieldName().equals(GeneralFieldTypes.UNIT.name)) {
                if (!protocolParameters.toString().equals("")) {
                    if (prevParameterIndex != -1) {
                        prevParameterIndex = -1;
                    }
                }
            } else {
                // end of protocol definition
                // construct a new protocol by adding what is in found :o)

                if (currentProtocol != null) {
                    constructProtocols(protocols, currentProtocol, protocolParameters.toString());
                    currentProtocol = null;
                }
                protocolParameters = new StringBuffer();
            }
            count++;
        }

        // add any remaining protocols
        if (currentProtocol != null) {
            constructProtocols(protocols, currentProtocol, protocolParameters.toString());
        }

        return protocols;
    }

    private List<FieldObject> getFields() {
        Collection<FieldObject> fields = tableReferenceObject.getFieldIndexLookup().values();

        List<FieldObject> fieldsAsList = new ArrayList<FieldObject>();
        fieldsAsList.addAll(fields);

        tableReferenceObject.sortFieldsByColumnNumber(fieldsAsList);
        return fieldsAsList;
    }

    private void constructProtocols(List<Protocol> protocols, FieldObject protocolField, String parameters) {
        Set<String> protocolNames = tableReferenceObject.getData().getDataInColumn(protocolField.getColNo());

        System.out.println("Adding protocol, " + protocolField.getDefaultVal());

        if (constructProtocolsWithDefaultValues) {
            protocols.add(new Protocol(protocolField.getDefaultVal(), protocolField.getDefaultVal(), "", "", "", parameters, "", ""));
        } else {
            for (String protocol : protocolNames) {
                protocols.add(new Protocol(protocol, protocolField.getDefaultVal(), "", "", "", parameters, "", ""));
            }
        }
    }

    public void setConstructProtocolsWithDefaultValues(boolean constructProtocolsWithDefaultValues) {
        this.constructProtocolsWithDefaultValues = constructProtocolsWithDefaultValues;
    }
}
