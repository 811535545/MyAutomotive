/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: D:\\SDK\\build-tools\\36.0.0\\aidl.exe -pD:\\SDK\\platforms\\android-37.0\\framework.aidl -oE:\\gitHub\\automotiveProject\\core\\aidl\\build\\generated\\aidl_source_output_dir\\debug\\out -IE:\\gitHub\\automotiveProject\\core\\aidl\\src\\main\\aidl -IE:\\gitHub\\automotiveProject\\core\\aidl\\src\\debug\\aidl -dC:\\Users\\Administrator\\AppData\\Local\\Temp\\aidl2185715333360904508.d E:\\gitHub\\automotiveProject\\core\\aidl\\src\\main\\aidl\\com\\oem\\car\\middleware\\IVehicleCoreManager.aidl
 *
 * DO NOT CHECK THIS FILE INTO A CODE TREE (e.g. git, etc..).
 * ALWAYS GENERATE THIS FILE FROM UPDATED AIDL COMPILER
 * AS A BUILD INTERMEDIATE ONLY. THIS IS NOT SOURCE CODE.
 */
package com.oem.car.middleware;
public interface IVehicleCoreManager extends android.os.IInterface
{
  /** Default implementation for IVehicleCoreManager. */
  public static class Default implements com.oem.car.middleware.IVehicleCoreManager
  {
    /**
     * 注册车辆事件监听器
     * 
     * 客户端通过此方法订阅车辆状态的变化。当车速、档位、电源状态等发生变化时，
     * 服务端将通过 listener 回调通知客户端。
     * 
     * @param listener 实现 IVehicleEventListener 接口的远程对象，用于接收状态更新回调。
     *                 建议使用 RemoteCallbackList 在服务端管理此监听器以防止内存泄漏。
     */
    @Override public void registerListener(com.oem.car.middleware.IVehicleEventListener listener) throws android.os.RemoteException
    {
    }
    /**
     * 注销车辆事件监听器
     * 
     * 当客户端不再需要接收车辆状态更新时（例如 Activity 销毁或 Service 停止），
     * 必须调用此方法以移除监听器，避免内存泄漏和无用的 Binder 调用。
     * 
     * @param listener 之前通过 registerListener 注册的同一个远程监听器对象。
     */
    @Override public void unregisterListener(com.oem.car.middleware.IVehicleEventListener listener) throws android.os.RemoteException
    {
    }
    /**
     * 注入模拟车速
     * 
     * 【测试/调试专用】强制设置当前车辆的速度值，覆盖真实传感器数据。
     * 此方法通常用于自动化测试、模拟器环境或开发调试阶段。
     * 
     * @param speed 模拟的车速值。单位通常为 km/h 或 m/s（具体取决于项目定义）。
     *              有效范围应根据实际车辆物理限制进行校验。
     */
    @Override public void injectMockSpeed(float speed) throws android.os.RemoteException
    {
    }
    /**
     * 注入模拟档位
     * 
     * 【测试/调试专用】强制设置当前的变速箱档位。
     * 
     * @param gear 模拟的档位整数值。
     *             常见映射示例（需参考具体常量定义）：
     *             0 - Neutral (空挡)
     *             1 - Drive (前进挡)
     *             2 - Reverse (倒挡)
     *             3 - Park (停车挡)
     */
    @Override public void injectMockGear(int gear) throws android.os.RemoteException
    {
    }
    /**
     * 注入模拟电源状态
     * 
     * 【测试/调试专用】强制设置车辆的电源/点火模式。
     * 
     * @param state 模拟的电源状态整数值。
     *              常见映射示例：
     *              0 - OFF (关闭)
     *              1 - ACC (附件通电)
     *              2 - ON (点火开启/仪表亮)
     *              3 - START (启动中)
     */
    @Override public void injectMockPower(int state) throws android.os.RemoteException
    {
    }
    /**
     * 触发 PTT (Push-To-Talk) 语音对讲
     * 
     * 模拟用户按下 PTT 按钮的行为。
     * 调用此方法将激活语音采集通道或向通信模块发送对讲请求。
     * 适用于车队通信、出租车调度或紧急对讲功能的测试与触发。
     */
    @Override public void triggerPttVoice() throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.oem.car.middleware.IVehicleCoreManager
  {
    /** Construct the stub and attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.oem.car.middleware.IVehicleCoreManager interface,
     * generating a proxy if needed.
     */
    public static com.oem.car.middleware.IVehicleCoreManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.oem.car.middleware.IVehicleCoreManager))) {
        return ((com.oem.car.middleware.IVehicleCoreManager)iin);
      }
      return new com.oem.car.middleware.IVehicleCoreManager.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_registerListener:
        {
          com.oem.car.middleware.IVehicleEventListener _arg0;
          _arg0 = com.oem.car.middleware.IVehicleEventListener.Stub.asInterface(data.readStrongBinder());
          this.registerListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterListener:
        {
          com.oem.car.middleware.IVehicleEventListener _arg0;
          _arg0 = com.oem.car.middleware.IVehicleEventListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_injectMockSpeed:
        {
          float _arg0;
          _arg0 = data.readFloat();
          this.injectMockSpeed(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_injectMockGear:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.injectMockGear(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_injectMockPower:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.injectMockPower(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_triggerPttVoice:
        {
          this.triggerPttVoice();
          reply.writeNoException();
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements com.oem.car.middleware.IVehicleCoreManager
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
       * 注册车辆事件监听器
       * 
       * 客户端通过此方法订阅车辆状态的变化。当车速、档位、电源状态等发生变化时，
       * 服务端将通过 listener 回调通知客户端。
       * 
       * @param listener 实现 IVehicleEventListener 接口的远程对象，用于接收状态更新回调。
       *                 建议使用 RemoteCallbackList 在服务端管理此监听器以防止内存泄漏。
       */
      @Override public void registerListener(com.oem.car.middleware.IVehicleEventListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      /**
       * 注销车辆事件监听器
       * 
       * 当客户端不再需要接收车辆状态更新时（例如 Activity 销毁或 Service 停止），
       * 必须调用此方法以移除监听器，避免内存泄漏和无用的 Binder 调用。
       * 
       * @param listener 之前通过 registerListener 注册的同一个远程监听器对象。
       */
      @Override public void unregisterListener(com.oem.car.middleware.IVehicleEventListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      /**
       * 注入模拟车速
       * 
       * 【测试/调试专用】强制设置当前车辆的速度值，覆盖真实传感器数据。
       * 此方法通常用于自动化测试、模拟器环境或开发调试阶段。
       * 
       * @param speed 模拟的车速值。单位通常为 km/h 或 m/s（具体取决于项目定义）。
       *              有效范围应根据实际车辆物理限制进行校验。
       */
      @Override public void injectMockSpeed(float speed) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeFloat(speed);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectMockSpeed, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      /**
       * 注入模拟档位
       * 
       * 【测试/调试专用】强制设置当前的变速箱档位。
       * 
       * @param gear 模拟的档位整数值。
       *             常见映射示例（需参考具体常量定义）：
       *             0 - Neutral (空挡)
       *             1 - Drive (前进挡)
       *             2 - Reverse (倒挡)
       *             3 - Park (停车挡)
       */
      @Override public void injectMockGear(int gear) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(gear);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectMockGear, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      /**
       * 注入模拟电源状态
       * 
       * 【测试/调试专用】强制设置车辆的电源/点火模式。
       * 
       * @param state 模拟的电源状态整数值。
       *              常见映射示例：
       *              0 - OFF (关闭)
       *              1 - ACC (附件通电)
       *              2 - ON (点火开启/仪表亮)
       *              3 - START (启动中)
       */
      @Override public void injectMockPower(int state) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(state);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectMockPower, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      /**
       * 触发 PTT (Push-To-Talk) 语音对讲
       * 
       * 模拟用户按下 PTT 按钮的行为。
       * 调用此方法将激活语音采集通道或向通信模块发送对讲请求。
       * 适用于车队通信、出租车调度或紧急对讲功能的测试与触发。
       */
      @Override public void triggerPttVoice() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_triggerPttVoice, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_registerListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_unregisterListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_injectMockSpeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_injectMockGear = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_injectMockPower = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_triggerPttVoice = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
  }
  /** @hide */
  public static final java.lang.String DESCRIPTOR = "com.oem.car.middleware.IVehicleCoreManager";
  /**
   * 注册车辆事件监听器
   * 
   * 客户端通过此方法订阅车辆状态的变化。当车速、档位、电源状态等发生变化时，
   * 服务端将通过 listener 回调通知客户端。
   * 
   * @param listener 实现 IVehicleEventListener 接口的远程对象，用于接收状态更新回调。
   *                 建议使用 RemoteCallbackList 在服务端管理此监听器以防止内存泄漏。
   */
  public void registerListener(com.oem.car.middleware.IVehicleEventListener listener) throws android.os.RemoteException;
  /**
   * 注销车辆事件监听器
   * 
   * 当客户端不再需要接收车辆状态更新时（例如 Activity 销毁或 Service 停止），
   * 必须调用此方法以移除监听器，避免内存泄漏和无用的 Binder 调用。
   * 
   * @param listener 之前通过 registerListener 注册的同一个远程监听器对象。
   */
  public void unregisterListener(com.oem.car.middleware.IVehicleEventListener listener) throws android.os.RemoteException;
  /**
   * 注入模拟车速
   * 
   * 【测试/调试专用】强制设置当前车辆的速度值，覆盖真实传感器数据。
   * 此方法通常用于自动化测试、模拟器环境或开发调试阶段。
   * 
   * @param speed 模拟的车速值。单位通常为 km/h 或 m/s（具体取决于项目定义）。
   *              有效范围应根据实际车辆物理限制进行校验。
   */
  public void injectMockSpeed(float speed) throws android.os.RemoteException;
  /**
   * 注入模拟档位
   * 
   * 【测试/调试专用】强制设置当前的变速箱档位。
   * 
   * @param gear 模拟的档位整数值。
   *             常见映射示例（需参考具体常量定义）：
   *             0 - Neutral (空挡)
   *             1 - Drive (前进挡)
   *             2 - Reverse (倒挡)
   *             3 - Park (停车挡)
   */
  public void injectMockGear(int gear) throws android.os.RemoteException;
  /**
   * 注入模拟电源状态
   * 
   * 【测试/调试专用】强制设置车辆的电源/点火模式。
   * 
   * @param state 模拟的电源状态整数值。
   *              常见映射示例：
   *              0 - OFF (关闭)
   *              1 - ACC (附件通电)
   *              2 - ON (点火开启/仪表亮)
   *              3 - START (启动中)
   */
  public void injectMockPower(int state) throws android.os.RemoteException;
  /**
   * 触发 PTT (Push-To-Talk) 语音对讲
   * 
   * 模拟用户按下 PTT 按钮的行为。
   * 调用此方法将激活语音采集通道或向通信模块发送对讲请求。
   * 适用于车队通信、出租车调度或紧急对讲功能的测试与触发。
   */
  public void triggerPttVoice() throws android.os.RemoteException;
}
