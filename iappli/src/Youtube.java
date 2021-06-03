import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.IApplication;
import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.MediaImage;
import com.nttdocomo.ui.MediaManager;
import com.nttdocomo.ui.TextBox;
import com.nttdocomo.ui.VisualPresenter;

/**
 * Youtube<BR>
 * �A�v���P�[�V�����̃��C���N���X�ł��B.
 * <br>
 * <p>
 * @version 1.0
 * </p>
 */
public final class Youtube extends IApplication {

    /**
     * <code>screen</code> ���C�����
     */
    private static Canvas screen = null;

    /**
     * <code>ENTRY_CGI</code> �o�^CGI
     */
    public static final String ENTRY_CGI = "entry.php";

    /**
     * <code>AUTH_CGI</code> �F��CGI
     */
    public static final String AUTH_CGI = "auth.php";

    /**
     * <code>TYPE_SOUND</code> �������ʎq
     * <code>TYPE_MOVIE</code> ���掯�ʎq
     * <code>TYPE_PICTURE</code> �Î~�掯�ʎq
     */
    public static final String
        TYPE_SOUND   = "WAV",
        TYPE_MOVIE   = "MOV",
        TYPE_PICTURE = "PIC";

    /**
     * <code>PINK  </code> �\���F�u�s���N�v��\���萔
     * <code>ORANGE</code> �\���F�u�I�����W�v��\���萔
     * <code>WHITE </code> �\���F�u���v��\���萔
     * <code>LIME  </code> �\���F�u���C���v��\���萔
     * <code>SILVER</code> �\���F�u��v��\���萔
     * <code>BLACK </code> �\���F�u���v��\���萔
     * <code>YELLOW</code> �\���F�u���F�v��\���萔
     * <code>GRAY  </code> �\���F�u�D�F�v��\���萔
     * <code>RED   </code> �\���F�u�ԁv��\���萔
     * <code>BLUE  </code> �\���F�u�v��\���萔
     */
    public static final int
        PINK   = Graphics.getColorOfRGB(248, 222, 194)   ,
        ORANGE = Graphics.getColorOfRGB(255, 128, 0)     ,
        WHITE  = Graphics.getColorOfName(Graphics.WHITE) ,
        LIME   = Graphics.getColorOfName(Graphics.LIME)  ,
        SILVER = Graphics.getColorOfName(Graphics.SILVER),
        BLACK  = Graphics.getColorOfName(Graphics.BLACK) ,
        YELLOW = Graphics.getColorOfName(Graphics.YELLOW),
        GRAY   = Graphics.getColorOfName(Graphics.GRAY)  ,
        RED    = Graphics.getColorOfName(Graphics.RED)   ,
        BLUE   = Graphics.getColorOfName(Graphics.BLUE);

    /**
     * �A�v���P�[�V�������N��������ŏ��ɌĂ΂�郁�\�b�h�ł��B
     */
    public void start() {
        try {
            // �ۑ��f�[�^�ǂݏo��
            ScratchPad.load();

            // �X�N���b�`�p�b�h�Ƀf�[�^���ۑ�����Ă��Ȃ�������H
            if(ScratchPad.length() == 0) {
                // ���C����ʂ̍쐬
                screen = new StartScreen();
            } else {
                // �X�^�[�g��ʃC���[�W�擾
                MediaData mail = ScratchPad.getMediaData(0);
                MediaData pass = ScratchPad.getMediaData(1);

                if (mail == null || pass == null) {
                    // ���C����ʂ̍쐬
                    screen = new StartScreen();
                } else {
                    // ���C����ʂ̍쐬
                    screen = new MainScreen(mail.getText(), pass.getText());
                }
            }

            // ���C����ʂ̕\��
            Display.setCurrent(screen);
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, "��Q���");
            dialog.setText(e.getClass().getName());
            dialog.show();
            terminate();
        }
    }

    /**
     * MainScreen<BR>
     * ���C����ʂ̒�`�N���X�ł��B
     * <p>
     * @version 1.0
     * </p>
     */
    private final class MainScreen extends Canvas {

        /**
         * <code>unSupportMovie</code> ���[�r�[���T�|�[�g���Ȃ��@��
         */
        private final String[] unSupportMovie = {
            "F900i",
            "F901i",
            "P900i",
            "P901i",
            "N900i",
            "N901i",
            "SH900i",
            "SH901i",
            "D800iDS",
            "M702iG",
            "M702iS"
        };

        /**
         * <code>back</code> �w�i�摜
         */
        private Image back = null;

        /**
         * <code>comment</code> �R�����g
         */
        private String comment = "";

        /**
         * <code>category</code> �J�e�S��
         */
        private int category = 0;

        /**
         * <code>categoryName</code> �J�e�S����
         */
        private final String[] categoryName = {
            "�����ԂƏ�蕨",
            "�R���f�B�[",
            "����",
            "�G���^�[�e�C�����g",
            "�f��ƃA�j��",
            "�Q�[��",
            "�n�E�c�[�ƃX�^�C��",
            "���y",
            "�j���[�X�Ɛ���",
            "��c���c�̂ƎЉ��",
            "�u���O�Ɛl",
            "�y�b�g�Ɠ���",
            "�Ȋw�ƋZ�p",
            "�X�|�[�c",
            "���s�ƃC�x���g"
        };

        /**
         * <code>categoryId</code> �J�e�S��ID
         */
        private final String[] categoryId = {
            "Autos",
            "Comedy",
            "Education",
            "Entertainment",
            "Film",
            "Games",
            "Howto",
            "Music",
            "News",
            "Nonprofit",
            "People",
            "Animals",
            "Tech",
            "Sports",
            "Travel"
        };

        /**
         * <code>function</code> �@�\
         */
        private int function = 0;

        /**
         * <code>functionList</code> �@�\���X�g
         */
        private String[] functionList;

        /**
         * <code>mode</code> ���[�h(���j���[�I��)
         */
        private int mode = 0;

        /**
         * <code>quality</code> �掿
         */
        private int quality = 0;

        /**
         * <code>qualityList</code> �掿���X�g
         */
        private String[] qualityList;

        /**
         * <code>visual</code> ����Đ��C���[�W
         */
        private VisualPresenter visual = new VisualPresenter();

        /**
         * <code>mail</code> ���[��
         */
        private String mail = "";

        /**
         * <code>password</code> �p�X���[�h
         */
        private String password = "";

        /**
         * <code>arrow</code> ���
         */
        private Image arrow = null;

        /**
         * <code>msg</code> ���b�Z�[�W
         */
        private Image msg = null;

        /**
         * �R���X�g���N�^
         * @param mail ���[��
         * @param password �p�X���[�h
         */
        public MainScreen(final String mail, final String password) {
            super();
            this.mail = mail;
            this.password = password;
            this.initScreen();
        }

        /**
         * ��ʏ���������
         */
        private void initScreen() {

            String[] args = IApplication.getCurrentApp().getArgs();
            String phoneName = System.getProperty("microedition.platform");
            boolean isSupport = true;

            try {
                MediaData md = ScratchPad.getResource(0);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    back = mi.getImage();
                }

                md = ScratchPad.getResource(5);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    arrow = mi.getImage();
                }

                md = ScratchPad.getResource(6);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    msg = mi.getImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // �@�픻��
            for (int i = 0; i < unSupportMovie.length; i++) {
                if (phoneName.indexOf(unSupportMovie[i]) != -1) {
                    isSupport = false;
                    break;
                }
            }

            // ���悪�T�|�[�g����Ă���ꍇ
            if (isSupport) {
                functionList = new String[2];
                functionList[0] = (char) 0xE677 + " �B��";
                functionList[1] = (char) 0xE677 + " �I��";
            } else {
                functionList = new String[1];
                functionList[0] = (char) 0xE677 + " �B��";
            }

            // �J�����T�C�Y�擾
            qualityList = new String[CameraDevice.MOV_SIZE.length];
            for (int i = 0; i < CameraDevice.MOV_SIZE.length; i++) {
                qualityList[i] = Integer.toString(CameraDevice.MOV_SIZE[i][0])
                               + " * "
                               + Integer.toString(CameraDevice.MOV_SIZE[i][1]);
            }
        }

        /**
         * �㉺�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * @param param �p�����[�^
         */
        private void componentActionLeftRight(final int param) {

            // �B�e�摜�m�F
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // �J�e�S���̏ꍇ
            if (this.mode == 0) {
                // ���L�[�̏ꍇ
                if (param == Display.KEY_LEFT) {
                    if (0 == this.category) {
                        this.category = this.categoryName.length - 1;
                    } else {
                        this.category--;
                    }

                // �E�L�[�̏ꍇ
                } else if (param == Display.KEY_RIGHT) {
                    if (this.categoryName.length == (this.category + 1)) {
                        this.category = 0;
                    } else {
                        this.category++;
                    }
                }

            // �@�\�̏ꍇ
            } else if (this.mode == 1) {
                // ���L�[�̏ꍇ
                if (param == Display.KEY_LEFT) {
                    if (this.function == 0) {
                        this.function = this.functionList.length - 1;
                    } else {
                        this.function--;
                    }

                // �E�L�[�̏ꍇ
                } else if (param == Display.KEY_RIGHT) {
                    if (this.functionList.length == (function + 1)) {
                        this.function = 0;
                    } else {
                        this.function++;
                    }
                }

                // �掿�N���A
                this.quality = 0;

                // ����̏ꍇ
                if (this.function == 0) {
                    this.qualityList = new String[CameraDevice.MOV_SIZE.length];
                    for (int i = 0; i < CameraDevice.MOV_SIZE.length; i++) {
                        this.qualityList[i] = Integer.toString(CameraDevice.MOV_SIZE[i][0])
                                             + " * "
                                             + Integer.toString(CameraDevice.MOV_SIZE[i][1]);
                    }

                // �I���̏ꍇ
                } else {
                    this.qualityList = new String[1];
                    this.qualityList[0] = "---------";
                }

            // �T�C�Y�̏ꍇ
            } else if (this.mode == 2) {
                // ���L�[�̏ꍇ
                if (param == Display.KEY_LEFT) {
                    if (quality == 0) {
                        this.quality = this.qualityList.length - 1;
                    } else {
                        this.quality--;
                    }

                // �E�L�[�̏ꍇ
                } else if (param == Display.KEY_RIGHT) {
                    if (this.qualityList.length == (quality + 1)) {
                        this.quality = 0;
                    } else {
                        this.quality++;
                    }
                }
            }
        }

        /**
         * �㉺�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * @param param �p�����[�^
         */
        private void componentActionUpDown(final int param) {

            // �B�e�摜�m�F
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // ��L�[�̏ꍇ
            if (param == Display.KEY_UP) {
                if (this.mode == 0) {
                    this.mode = 2;
                } else {
                    this.mode--;
                }

            // ���L�[�̏ꍇ
            } else if (param == Display.KEY_DOWN) {
                if (this.mode == 2) {
                    this.mode = 0;
                } else {
                    this.mode++;
                }
            }
        }

        /**
         * �`��C�x���g
         * @see com.nttdocomo.ui.Canvas#paint(com.nttdocomo.ui.Graphics)
         */
        public void paint(final Graphics g) {

            // �`��X�g�b�v
            g.lock();

            if (back != null) {
                g.drawImage(back, 0, 0);
            }

            if (arrow != null) {
                if (this.mode == 0) {
                    g.drawImage(arrow, 215, 179);
                } else if (this.mode == 1) {
                    g.drawImage(arrow, 130, 201);
                } else if (this.mode == 2) {
                    g.drawImage(arrow, 130, 221);
                }
            }

            // �R�����g�������ꍇ
            g.setColor(GRAY);
            if (this.comment.length() > 14) {
                g.drawString(this.comment.substring(0, 14), 55, 170);
            } else {
                g.drawString(this.comment, 55, 170);
            }

            // �B�e�摜�m�F
            if (CameraDevice.getNumberOfImages() > 0) {
                // ���b�Z�[�W�\��
                g.drawImage(msg, 54, 80);

                // �\�t�g�L�[���x���̐ݒ�
                setSoftLabel(SOFT_KEY_1, "����");
                setSoftLabel(SOFT_KEY_2, "����");
            } else {
                // �\�t�g�L�[���x���̐ݒ�
                setSoftLabel(SOFT_KEY_1, "�I��");
                if (this.function == 0) {
                    setSoftLabel(SOFT_KEY_2, "�B��");
                } else {
                    setSoftLabel(SOFT_KEY_2, "�I��");
                }
            }

            // �@�\�`��
            g.setColor(GRAY);
            g.setPictoColorEnabled(true);
            g.drawString(this.functionList[this.function], 70, 212);
            g.setPictoColorEnabled(false);

            // �T�C�Y�`��
            g.setColor(GRAY);
            g.drawString(this.qualityList[this.quality], 64, 234);
            g.drawString(this.categoryName[this.category], 55, 191);

            // �`��
            g.unlock(true);
        }

        /**
         * �L�[�C�x���g
         * @see com.nttdocomo.ui.Canvas#processEvent(int, int)
         */
        public void processEvent(final int type, final int param) {
            if (type == Display.KEY_RELEASED_EVENT) {
                switch(param) {

                // �E���L�[
                case Display.KEY_LEFT:
                case Display.KEY_RIGHT:
                    this.componentActionLeftRight(param);
                    this.repaint();
                    break;

                // �㉺�L�[
                case Display.KEY_DOWN:
                case Display.KEY_UP:
                    this.componentActionUpDown(param);
                    this.repaint();
                    break;

                // �\�t�g�L�[
                case Display.KEY_SOFT1:
                case Display.KEY_SOFT2:
                    this.softKeyReleased(param);
                    this.repaint();
                    break;

                // ����L�[
                case Display.KEY_SELECT:
                    imeOn(this.comment, TextBox.DISPLAY_ANY, TextBox.KANA);
                    break;

                // �����̂P�L�[
                case Display.KEY_1:
                    launch(LAUNCH_BROWSER, new String[]{"http://m.youtube.com/"});
                    break;

                // �����̂Q�L�[
                case Display.KEY_2:
                    // �J�����C���[�W�擾
                    MediaImage image = CameraDevice.getMediaImage();

                    // �C���[�W���擾�ł����ꍇ
                    if (image != null) {
                        visual.setImage(image);
                        visual.play();
                    }
                    break;
                default:
                }
            }
        }

        /**
         * IME�C�x���g
         * @see com.nttdocomo.ui.Canvas#processIMEEvent(int, java.lang.String)
         */
        public void processIMEEvent(final int type, final String text) {
            if (type == IME_COMMITTED) {
                this.comment = text;
                this.repaint();
            }
        }

        /**
         * �摜�𑗐M����B
         */
        private void send() {
            boolean result = false;
            MediaData md = null;

            // ���f�B�A�f�[�^�ɓ����
            md = new MediaData(CameraDevice.getCameraData(),
                               TYPE_MOVIE,
                               this.comment);
            // �f�[�^���M
            result = Communication.sendData(
                         getSourceURL() + ENTRY_CGI,
                         md.getType(),
                         this.mail,
                         this.password,
                         this.categoryId[this.category],
                         this.comment,
                         md.toInputStream());

            // �ʐM���ʔ���
            if (result) {
                // �c���Ă���f�[�^���폜
                CameraDevice.dispose();
            } else {
                Dialog dialog = null;
                dialog = new Dialog(Dialog.DIALOG_WARNING, "�ʐM��Q");
                dialog.setText("�ʐM���ɃG���[���������܂����B\n�d�g�󋵂̗ǂ��Ƃ���ōđ����������B\n");
            }

            // ��ʂ�߂�
            this.show();
        }

        /**
         * ��ʂ�\��
         */
        public void show() {
            Display.setCurrent(this);
        }

        /**
         * �������ꂽ�\�t�g�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * ���̃��\�b�h�́A<code>setSoftKeyListener()</code>���g����
         * �\�t�g�L�[���X�i�[��o�^���Ă���ꍇ�ɗL���ƂȂ�܂��B
         * @param key �����ꂽ�L�[
         */
        private void softKeyReleased(final int key) {

            // �I���L�[����
            if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() == 0) {

                // �A�v���I��
                IApplication.getCurrentApp().terminate();

            // �B�e�L�[����
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() == 0) {
                // ����E�Î~��𔻒�
                if (this.function == 0) {
                    CameraDevice.takeMovie(this.quality);
                } else {
                    CameraDevice.selectMovie();
                }

            // ����L�[����
            } else if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() != 0) {
                // �c���Ă���f�[�^���폜
                CameraDevice.dispose();

            // ���M�L�[����
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() != 0) {
                // �f�[�^���M
                this.send();
            }
        }
    }

    /**
     * StartScreen<BR>
     * �J�n��ʂ̒�`�N���X�ł��B
     * <p>
     * @version 1.0
     * </p>
     */
    private final class StartScreen extends Canvas {

        /**
         * <code>back</code> �w�i�摜
         */
        private Image back = null;

        /**
         * <code>arrow</code> ���
         */
        private Image arrow = null;

        /**
         * <code>mail</code> ���[��
         */
        private String mail = "";

        /**
         * <code>password</code> �p�X���[�h
         */
        private String password = "";

        /**
         * <code>mode</code> ���[�h(���[���E�p�X���[�h/�I��)
         */
        private boolean mode = true;

        /**
         * �R���X�g���N�^
         */
        public StartScreen() {
            super();

            try {

                // �X�^�[�g��ʃC���[�W�擾
                MediaData md = ScratchPad.getResource(3);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    back = mi.getImage();
                }

                // ���C���[�W�擾
                md = ScratchPad.getResource(4);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    arrow = mi.getImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * �㉺�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * @param param �p�����[�^
         */
        private void componentActionUpDown(final int param) {

            // �㉺�L�[�̏ꍇ
            if (param == Display.KEY_UP || param == Display.KEY_DOWN) {
                this.mode = !this.mode;
            }
        }

        /**
         * �`��C�x���g
         * @see com.nttdocomo.ui.Canvas#paint(com.nttdocomo.ui.Graphics)
         */
        public void paint(final Graphics g) {

            // �`��X�g�b�v
            g.lock();

            if (back != null) {
                g.drawImage(back, 0, 0);
            }

            if (arrow != null) {
                if (this.mode) {
                    g.drawImage(arrow, 225, 173);
                } else {
                    g.drawImage(arrow, 225, 205);
                }
            }

            // �R�����g�������ꍇ
            g.setColor(GRAY);
            setSoftLabel(SOFT_KEY_1, "�I��");
            setSoftLabel(SOFT_KEY_2, "����");

            // �@�\�`��
            g.drawString(this.mail, 60, 186);
            if (this.password.length() > 0) {
                g.drawString("**************", 60, 217);
            }

            // �`��
            g.unlock(true);
        }

        /**
         * �L�[�C�x���g
         * @see com.nttdocomo.ui.Canvas#processEvent(int, int)
         */
        public void processEvent(final int type, final int param) {
            if (type == Display.KEY_RELEASED_EVENT) {
                switch(param) {

                // �㉺�L�[
                case Display.KEY_DOWN:
                case Display.KEY_UP:
                    this.componentActionUpDown(param);
                    this.repaint();
                    break;

                // �\�t�g�L�[
                case Display.KEY_SOFT1:
                case Display.KEY_SOFT2:
                    this.softKeyReleased(param);
                    this.repaint();
                    break;

                // ����L�[
                case Display.KEY_SELECT:
                    if (this.mode) {
                        imeOn(this.mail, TextBox.DISPLAY_ANY, TextBox.ALPHA);
                    } else {
                        imeOn(this.password, TextBox.DISPLAY_ANY, TextBox.ALPHA);
                    }

                    break;
                default:
                }
            }
        }

        /**
         * IME�C�x���g
         * @see com.nttdocomo.ui.Canvas#processIMEEvent(int, java.lang.String)
         */
        public void processIMEEvent(final int type, final String text) {
            if (type == IME_COMMITTED) {

                if (this.mode) {
                    this.mail = text;
                } else {
                    this.password = text;
                }

                this.repaint();
            }
        }

        /**
         * �������ꂽ�\�t�g�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * ���̃��\�b�h�́A<code>setSoftKeyListener()</code>���g����
         * �\�t�g�L�[���X�i�[��o�^���Ă���ꍇ�ɗL���ƂȂ�܂��B
         * @param key �����ꂽ�L�[
         */
        private void softKeyReleased(final int key) {

            // ����L�[����
            if (Display.KEY_SOFT1 == key) {
                // �A�v���I��
                IApplication.getCurrentApp().terminate();
            // ���M�L�[����
            } else if (Display.KEY_SOFT2 == key) {
                send();
            }
        }

        /**
         * �F�؂𑗐M����B
         */
        private void send() {
            // �f�[�^���M
            String result = Communication.authorize(getSourceURL() + AUTH_CGI, this.mail, this.password);

            if (result == null) {
                Dialog dialog = new Dialog(Dialog.DIALOG_WARNING, "���[�U�F��");
                dialog.setText("���[�U�F�؂ŃG���[���������܂����B\n�A�J�E���g���L�����m�F���Ă��������B\n");
                dialog.show();
                // ��ʂ�߂�
                this.show();
            } else {
                ScratchPad.addImage(new MediaData(this.mail));
                ScratchPad.addImage(new MediaData(this.password));
                if (ScratchPad.save()){
                    // ���C����ʂ̍쐬
                    screen = new MainScreen(this.mail, this.password);
                    // ���C����ʂ̕\��
                    Display.setCurrent(screen);
                }
            }
        }

        /**
         * ��ʂ�\��
         */
        public void show() {
            Display.setCurrent(this);
        }
    }
}
