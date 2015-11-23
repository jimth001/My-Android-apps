package com.example.bluetoothcar;
import com.bn.sample11_3.R;

import android.R.integer;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class MainActivity extends Activity {
    private String myDeviceName;
    
    //UI��Ϣ��ʾ�豸����
	public static final String DeviceName = null;
	
	//������������������
	private BluetoothAdapter btAdapter = null;
	private MyService myService = null;
	//��¼С������ķ���
	private MyAutoControl myControl = null;
	//������������handler��Ϣ���̼߳�ͨ��
    public static final int Message = 1;
    public static final int State_Change = 96;
    public static final int Device_Name = 4;
    
    //�������豸
    private static final int Enable_Bluetooth = 2;
    
    static float screenStanderWidth=960;
	static float screenStanderHeight=540;
	
	static float c=screenStanderWidth/2;
	static float d=screenStanderHeight/2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		MySurfaceView mv=new MySurfaceView(this);
        setContentView(mv);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
        	Toast.makeText(this, R.string.bt_not_available, Toast.LENGTH_LONG).show();
        	return;
        }
        if(myControl==null)
    	{
    		myControl=new MyAutoControl(this.getApplicationContext(),mHandler);
    	}
        myControl.start();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	if(myControl==null)
    	{
    		myControl=new MyAutoControl(this.getApplicationContext(),mHandler);
    	}
    	if(!btAdapter.isEnabled()) {
    		Intent btintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(btintent, Enable_Bluetooth);
    	}
    		else if(myService == null) {
    			myService = new MyService(this, mHandler,myControl.getMyAutoControlHandler());
    		}
    	if(!myControl.isAlive())
    	{
    		Toast.makeText(getApplicationContext(), "myControl����״̬�쳣"
                    , Toast.LENGTH_SHORT).show();
    	}
        }
		
    @Override
    public synchronized void onResume() {
    	super.onResume();
    	if(myService != null) {
    		if(myService.getState() == MyService.STATE_NONE) {
    			myService.start();
    		}
    	}
    	if(myControl!=null){
    		myControl.reSet();
    	}
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if(btAdapter.isEnabled()) {
        	//sendMessage((byte)0x00);//.........151104
        }
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void onDestroy() {
        super.onDestroy();
        if (myService != null) myService.stop();
        //if(myControl!=null) myControl.destroy();
    }
   
	public void mySendMessage(byte b) {
		if (myService.getState() != MyService.STATE_CONNECTED) {
			Toast.makeText(getApplicationContext(), "��δ���ӵ��豸"
                    , Toast.LENGTH_SHORT).show();
            return;
        }
		if(myControl.isAuto()==false)//��������Զ�����״̬����������
            {myService.write(b);}
        
	}
   
    //���������¼������ؽ��
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {//requestCode��ʶ���ĸ�Activity��ת����Activity ��startActivityForResult�е�requestCode���Ӧ 
		case 1://1���������豸  //resultCode��ʾ����ֵ״̬ ����Activityͨ����setResult()��������       data�����˷�������
			// ����豸�б�Activity����һ�����ӵ��豸
			if (resultCode == Activity.RESULT_OK) {
				// ��ȡ�豸��MAC��ַ
				String address = data.getExtras().getString(
						MyDeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// ��ȡBLuetoothDevice����
				BluetoothDevice device = btAdapter.getRemoteDevice(address);
				myService.connect(device);// ���Ӹ��豸
			}
			break;
		case Enable_Bluetooth:
          if (resultCode == Activity.RESULT_OK) {
        	  if(myControl==null) myControl=new MyAutoControl(this.getApplicationContext(),mHandler);
        	  myService = new MyService(this, mHandler,myControl.getMyAutoControlHandler());
        	  // ��ʼ��BluetoothService��ִ����������
              //Handler����mHandler���������ݵĽ������߳�֮���ͨ��
             } 
          else {
            Toast.makeText(this, R.string.bt_not_enable, Toast.LENGTH_SHORT).show();
            return;
            }
		}
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// �����豸�б�Activity
		Intent serverIntent = new Intent(this, MyDeviceListActivity.class);
		startActivityForResult(serverIntent, 1);//1�뷽��onActivityResult�е�1���Ӧ
		return true;
	}	
  //����handler��ͬ����Ϣ
    private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			 switch (msg.what){
	            case Device_Name:
	                myDeviceName = msg.getData().getString(DeviceName);
	                Toast.makeText(getApplicationContext(), "�����ӵ� "
	                               + myDeviceName, Toast.LENGTH_SHORT).show();
	                break;
					case MyAutoControl.angle0:mySendMessage(MyAutoControl.Turnangle0);break;
					case MyAutoControl.angle1:mySendMessage(MyAutoControl.Turnangle1);break;
					case MyAutoControl.angle2:mySendMessage(MyAutoControl.Turnangle2);break;
					case MyAutoControl.angle3:mySendMessage(MyAutoControl.Turnangle3);break;
					case MyAutoControl.angle4:mySendMessage(MyAutoControl.Turnangle4);break;
					case MyAutoControl.angle5:mySendMessage(MyAutoControl.Turnangle5);break;
					case MyAutoControl.angle6:mySendMessage(MyAutoControl.Turnangle6);break;
					case MyAutoControl.angle7:mySendMessage(MyAutoControl.Turnangle7);break;
					case MyAutoControl.angle8:mySendMessage(MyAutoControl.Turnangle8);break;
					case MyAutoControl.angle9:mySendMessage(MyAutoControl.Turnangle9);break;
					case MyAutoControl.up:mySendMessage(MyAutoControl.Turnup);break;
					case MyAutoControl.down:mySendMessage(MyAutoControl.Turndown);break;
					case MyAutoControl.forward:mySendMessage(MyAutoControl.Turnforward);break;
					case MyAutoControl.back:mySendMessage(MyAutoControl.Turnback);break;
					case MyAutoControl.stop:mySendMessage(MyAutoControl.Turnstop);break;
					case MyAutoControl.optTips:Toast.makeText(getApplicationContext(), msg.obj.toString()
                            , Toast.LENGTH_SHORT).show();break;
					case MyAutoControl.fileNotExist:Toast.makeText(getApplicationContext(), "��¼�ļ������ڣ����ȼ�¼����"
                            , Toast.LENGTH_SHORT).show();break;
					default: /*Toast.makeText(getApplicationContext(), "�Զ�����ָ�����"+msg.what+" "+msg.arg1+" "+msg.arg2
                            , Toast.LENGTH_SHORT).show();*/
						break;
	            }
		}
    	
    };
    public void sendMessageToAuto(int s)
    {
    	if(myService.getState()==myService.STATE_CONNECTED)
    	myService.autoHandler.obtainMessage(s,0,0,null).sendToTarget();
    	else {
    		Toast.makeText(getApplicationContext(), "��δ���ӵ��豸"
                    , Toast.LENGTH_SHORT).show();
		}
    }

}