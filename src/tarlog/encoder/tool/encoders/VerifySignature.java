package tarlog.encoder.tool.encoders;

import java.security.Signature;
import java.security.cert.Certificate;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tarlog.encoder.tool.KeyStoreAwareEncoder;
import tarlog.encoder.tool.Utils;

public class VerifySignature extends KeyStoreAwareEncoder {

    @Override
    public String getEncoderName() {
        return "Verify Signature";
    }

    @Override
    public Object encode(byte[] source) {
        if (keystore == null) {
            Utils.showErrorMessage(shell, "Error",
                "Key store should be initialized");
            return null;
        }
        SigDetailsDialog inputDialog = new SigDetailsDialog(shell);
        int rc = inputDialog.open();
        if (rc != Dialog.OK) {
            return null;
        }
        try {
            Signature sig = Signature.getInstance(algorithm);
            Certificate certificate = keystore.getCertificate(alias);
            sig.initVerify(certificate.getPublicKey());
            sig.update(source);
            return String.valueOf(sig.verify(Utils.bytesFromHex(signature)));
        } catch (Exception e) {
            Utils.showException(shell, e);
            return null;
        }
    }

    private String algorithm;
    private String alias;
    private String signature;

    public class SigDetailsDialog extends Dialog {

        private Combo uiAlgorithm;
        private Text  uiAlias;
        private Text  uiSignature;

        public SigDetailsDialog(Shell parent) {
            super(parent);
        }

        protected void configureShell(Shell shell) {
            super.configureShell(shell);
            shell.setText("Select Signature Details");
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = new Composite(
                (Composite) super.createDialogArea(parent), SWT.NONE);
            composite.setLayout(new GridLayout(2, false));
            Label label = new Label(composite, SWT.NONE);
            label.setText("Algorithm: ");
            uiAlgorithm = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
            uiAlgorithm.add("SHA1withDSA");
            uiAlgorithm.add("SHA1withRSA");
            if (algorithm != null) {
                uiAlgorithm.setText(algorithm);
            } else {
                uiAlgorithm.setText("SHA1withDSA");
            }
            label = new Label(composite, SWT.NONE);
            label.setText("Certificate: ");
            uiAlias = new Text(composite, SWT.SINGLE | SWT.BORDER);
            if (alias != null) {
                uiAlias.setText(alias);
            }

            label = new Label(composite, SWT.NONE);
            label.setText("Signature (as bytes): ");
            uiSignature = new Text(composite, SWT.MULTI | SWT.BORDER);
            GridData layoutData = new GridData(GridData.FILL_BOTH);
            layoutData.verticalSpan = 3;
            uiSignature.setLayoutData(layoutData);
            if (signature != null) {
                uiSignature.setText(signature);
            }
            return composite;
        }

        protected void buttonPressed(int buttonId) {
            if (buttonId == IDialogConstants.OK_ID) {
                algorithm = uiAlgorithm.getText();
                alias = uiAlias.getText();
                signature = uiSignature.getText();
            }
            super.buttonPressed(buttonId);
        }

    }

}