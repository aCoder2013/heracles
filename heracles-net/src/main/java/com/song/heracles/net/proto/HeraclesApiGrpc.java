package com.song.heracles.net.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.9.0)",
    comments = "Source: heraclesApi.proto")
public final class HeraclesApiGrpc {

  private HeraclesApiGrpc() {}

  private static <T> io.grpc.stub.StreamObserver<T> toObserver(final io.vertx.core.Handler<io.vertx.core.AsyncResult<T>> handler) {
    return new io.grpc.stub.StreamObserver<T>() {
      private volatile boolean resolved = false;
      @Override
      public void onNext(T value) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture(value));
        }
      }

      @Override
      public void onError(Throwable t) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.failedFuture(t));
        }
      }

      @Override
      public void onCompleted() {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture());
        }
      }
    };
  }

  public static final String SERVICE_NAME = "com.song.fastmq.broker.net.HeraclesApi";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getHandleProducerConnectMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest,
      com.song.heracles.net.proto.HeraclesProto.ProducerResponse> METHOD_HANDLE_PRODUCER_CONNECT = getHandleProducerConnectMethod();

  private static volatile io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest,
      com.song.heracles.net.proto.HeraclesProto.ProducerResponse> getHandleProducerConnectMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest,
      com.song.heracles.net.proto.HeraclesProto.ProducerResponse> getHandleProducerConnectMethod() {
    io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest, com.song.heracles.net.proto.HeraclesProto.ProducerResponse> getHandleProducerConnectMethod;
    if ((getHandleProducerConnectMethod = HeraclesApiGrpc.getHandleProducerConnectMethod) == null) {
      synchronized (HeraclesApiGrpc.class) {
        if ((getHandleProducerConnectMethod = HeraclesApiGrpc.getHandleProducerConnectMethod) == null) {
          HeraclesApiGrpc.getHandleProducerConnectMethod = getHandleProducerConnectMethod = 
              io.grpc.MethodDescriptor.<com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest, com.song.heracles.net.proto.HeraclesProto.ProducerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.song.fastmq.broker.net.HeraclesApi", "handleProducerConnect"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.ProducerResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new HeraclesApiMethodDescriptorSupplier("handleProducerConnect"))
                  .build();
          }
        }
     }
     return getHandleProducerConnectMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getHandleSendMessageMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.SendMessageRequest,
      com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> METHOD_HANDLE_SEND_MESSAGE = getHandleSendMessageMethod();

  private static volatile io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.SendMessageRequest,
      com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> getHandleSendMessageMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.SendMessageRequest,
      com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> getHandleSendMessageMethod() {
    io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.SendMessageRequest, com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> getHandleSendMessageMethod;
    if ((getHandleSendMessageMethod = HeraclesApiGrpc.getHandleSendMessageMethod) == null) {
      synchronized (HeraclesApiGrpc.class) {
        if ((getHandleSendMessageMethod = HeraclesApiGrpc.getHandleSendMessageMethod) == null) {
          HeraclesApiGrpc.getHandleSendMessageMethod = getHandleSendMessageMethod = 
              io.grpc.MethodDescriptor.<com.song.heracles.net.proto.HeraclesProto.SendMessageRequest, com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.song.fastmq.broker.net.HeraclesApi", "handleSendMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.SendMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.SendMessageResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new HeraclesApiMethodDescriptorSupplier("handleSendMessage"))
                  .build();
          }
        }
     }
     return getHandleSendMessageMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getHandleConsumerConnectMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest,
      com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> METHOD_HANDLE_CONSUMER_CONNECT = getHandleConsumerConnectMethod();

  private static volatile io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest,
      com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> getHandleConsumerConnectMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest,
      com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> getHandleConsumerConnectMethod() {
    io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest, com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> getHandleConsumerConnectMethod;
    if ((getHandleConsumerConnectMethod = HeraclesApiGrpc.getHandleConsumerConnectMethod) == null) {
      synchronized (HeraclesApiGrpc.class) {
        if ((getHandleConsumerConnectMethod = HeraclesApiGrpc.getHandleConsumerConnectMethod) == null) {
          HeraclesApiGrpc.getHandleConsumerConnectMethod = getHandleConsumerConnectMethod = 
              io.grpc.MethodDescriptor.<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest, com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.song.fastmq.broker.net.HeraclesApi", "handleConsumerConnect"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new HeraclesApiMethodDescriptorSupplier("handleConsumerConnect"))
                  .build();
          }
        }
     }
     return getHandleConsumerConnectMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getHandleConsumerPullMessageMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest,
      com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> METHOD_HANDLE_CONSUMER_PULL_MESSAGE = getHandleConsumerPullMessageMethod();

  private static volatile io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest,
      com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> getHandleConsumerPullMessageMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest,
      com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> getHandleConsumerPullMessageMethod() {
    io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest, com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> getHandleConsumerPullMessageMethod;
    if ((getHandleConsumerPullMessageMethod = HeraclesApiGrpc.getHandleConsumerPullMessageMethod) == null) {
      synchronized (HeraclesApiGrpc.class) {
        if ((getHandleConsumerPullMessageMethod = HeraclesApiGrpc.getHandleConsumerPullMessageMethod) == null) {
          HeraclesApiGrpc.getHandleConsumerPullMessageMethod = getHandleConsumerPullMessageMethod = 
              io.grpc.MethodDescriptor.<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest, com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.song.fastmq.broker.net.HeraclesApi", "handleConsumerPullMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new HeraclesApiMethodDescriptorSupplier("handleConsumerPullMessage"))
                  .build();
          }
        }
     }
     return getHandleConsumerPullMessageMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getHandlePullOffsetMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest,
      com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> METHOD_HANDLE_PULL_OFFSET = getHandlePullOffsetMethod();

  private static volatile io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest,
      com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> getHandlePullOffsetMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest,
      com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> getHandlePullOffsetMethod() {
    io.grpc.MethodDescriptor<com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest, com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> getHandlePullOffsetMethod;
    if ((getHandlePullOffsetMethod = HeraclesApiGrpc.getHandlePullOffsetMethod) == null) {
      synchronized (HeraclesApiGrpc.class) {
        if ((getHandlePullOffsetMethod = HeraclesApiGrpc.getHandlePullOffsetMethod) == null) {
          HeraclesApiGrpc.getHandlePullOffsetMethod = getHandlePullOffsetMethod = 
              io.grpc.MethodDescriptor.<com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest, com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.song.fastmq.broker.net.HeraclesApi", "handlePullOffset"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new HeraclesApiMethodDescriptorSupplier("handlePullOffset"))
                  .build();
          }
        }
     }
     return getHandlePullOffsetMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static HeraclesApiStub newStub(io.grpc.Channel channel) {
    return new HeraclesApiStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static HeraclesApiBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new HeraclesApiBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static HeraclesApiFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new HeraclesApiFutureStub(channel);
  }

  /**
   * Creates a new vertx stub that supports all call types for the service
   */
  public static HeraclesApiVertxStub newVertxStub(io.grpc.Channel channel) {
    return new HeraclesApiVertxStub(channel);
  }

  /**
   */
  public static abstract class HeraclesApiImplBase implements io.grpc.BindableService {

    /**
     */
    public void handleProducerConnect(com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ProducerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHandleProducerConnectMethod(), responseObserver);
    }

    /**
     */
    public void handleSendMessage(com.song.heracles.net.proto.HeraclesProto.SendMessageRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHandleSendMessageMethod(), responseObserver);
    }

    /**
     */
    public void handleConsumerConnect(com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHandleConsumerConnectMethod(), responseObserver);
    }

    /**
     */
    public void handleConsumerPullMessage(com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHandleConsumerPullMessageMethod(), responseObserver);
    }

    /**
     */
    public void handlePullOffset(com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHandlePullOffsetMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getHandleProducerConnectMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest,
                com.song.heracles.net.proto.HeraclesProto.ProducerResponse>(
                  this, METHODID_HANDLE_PRODUCER_CONNECT)))
          .addMethod(
            getHandleSendMessageMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.SendMessageRequest,
                com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>(
                  this, METHODID_HANDLE_SEND_MESSAGE)))
          .addMethod(
            getHandleConsumerConnectMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest,
                com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>(
                  this, METHODID_HANDLE_CONSUMER_CONNECT)))
          .addMethod(
            getHandleConsumerPullMessageMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest,
                com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>(
                  this, METHODID_HANDLE_CONSUMER_PULL_MESSAGE)))
          .addMethod(
            getHandlePullOffsetMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest,
                com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>(
                  this, METHODID_HANDLE_PULL_OFFSET)))
          .build();
    }
  }

  /**
   */
  public static final class HeraclesApiStub extends io.grpc.stub.AbstractStub<HeraclesApiStub> {
    public HeraclesApiStub(io.grpc.Channel channel) {
      super(channel);
    }

    public HeraclesApiStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeraclesApiStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeraclesApiStub(channel, callOptions);
    }

    /**
     */
    public void handleProducerConnect(com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ProducerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHandleProducerConnectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleSendMessage(com.song.heracles.net.proto.HeraclesProto.SendMessageRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHandleSendMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleConsumerConnect(com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHandleConsumerConnectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleConsumerPullMessage(com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHandleConsumerPullMessageMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handlePullOffset(com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest request,
        io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHandlePullOffsetMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class HeraclesApiBlockingStub extends io.grpc.stub.AbstractStub<HeraclesApiBlockingStub> {
    public HeraclesApiBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    public HeraclesApiBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeraclesApiBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeraclesApiBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.song.heracles.net.proto.HeraclesProto.ProducerResponse handleProducerConnect(com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest request) {
      return blockingUnaryCall(
          getChannel(), getHandleProducerConnectMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.song.heracles.net.proto.HeraclesProto.SendMessageResponse handleSendMessage(com.song.heracles.net.proto.HeraclesProto.SendMessageRequest request) {
      return blockingUnaryCall(
          getChannel(), getHandleSendMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse handleConsumerConnect(com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest request) {
      return blockingUnaryCall(
          getChannel(), getHandleConsumerConnectMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse handleConsumerPullMessage(com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest request) {
      return blockingUnaryCall(
          getChannel(), getHandleConsumerPullMessageMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse handlePullOffset(com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest request) {
      return blockingUnaryCall(
          getChannel(), getHandlePullOffsetMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class HeraclesApiFutureStub extends io.grpc.stub.AbstractStub<HeraclesApiFutureStub> {
    public HeraclesApiFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    public HeraclesApiFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeraclesApiFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeraclesApiFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.song.heracles.net.proto.HeraclesProto.ProducerResponse> handleProducerConnect(
        com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHandleProducerConnectMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> handleSendMessage(
        com.song.heracles.net.proto.HeraclesProto.SendMessageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHandleSendMessageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> handleConsumerConnect(
        com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHandleConsumerConnectMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> handleConsumerPullMessage(
        com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHandleConsumerPullMessageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> handlePullOffset(
        com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHandlePullOffsetMethod(), getCallOptions()), request);
    }
  }

  /**
   */
  public static abstract class HeraclesApiVertxImplBase implements io.grpc.BindableService {

    /**
     */
    public void handleProducerConnect(com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest request,
        io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.ProducerResponse> response) {
      asyncUnimplementedUnaryCall(getHandleProducerConnectMethod(), HeraclesApiGrpc.toObserver(response.completer()));
    }

    /**
     */
    public void handleSendMessage(com.song.heracles.net.proto.HeraclesProto.SendMessageRequest request,
        io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse> response) {
      asyncUnimplementedUnaryCall(getHandleSendMessageMethod(), HeraclesApiGrpc.toObserver(response.completer()));
    }

    /**
     */
    public void handleConsumerConnect(com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest request,
        io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse> response) {
      asyncUnimplementedUnaryCall(getHandleConsumerConnectMethod(), HeraclesApiGrpc.toObserver(response.completer()));
    }

    /**
     */
    public void handleConsumerPullMessage(com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest request,
        io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse> response) {
      asyncUnimplementedUnaryCall(getHandleConsumerPullMessageMethod(), HeraclesApiGrpc.toObserver(response.completer()));
    }

    /**
     */
    public void handlePullOffset(com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest request,
        io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse> response) {
      asyncUnimplementedUnaryCall(getHandlePullOffsetMethod(), HeraclesApiGrpc.toObserver(response.completer()));
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getHandleProducerConnectMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest,
                com.song.heracles.net.proto.HeraclesProto.ProducerResponse>(
                  this, METHODID_HANDLE_PRODUCER_CONNECT)))
          .addMethod(
            getHandleSendMessageMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.SendMessageRequest,
                com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>(
                  this, METHODID_HANDLE_SEND_MESSAGE)))
          .addMethod(
            getHandleConsumerConnectMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest,
                com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>(
                  this, METHODID_HANDLE_CONSUMER_CONNECT)))
          .addMethod(
            getHandleConsumerPullMessageMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest,
                com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>(
                  this, METHODID_HANDLE_CONSUMER_PULL_MESSAGE)))
          .addMethod(
            getHandlePullOffsetMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest,
                com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>(
                  this, METHODID_HANDLE_PULL_OFFSET)))
          .build();
    }
  }

  /**
   */
  public static final class HeraclesApiVertxStub extends io.grpc.stub.AbstractStub<HeraclesApiVertxStub> {
    public HeraclesApiVertxStub(io.grpc.Channel channel) {
      super(channel);
    }

    public HeraclesApiVertxStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HeraclesApiVertxStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new HeraclesApiVertxStub(channel, callOptions);
    }

    /**
     */
    public void handleProducerConnect(com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<com.song.heracles.net.proto.HeraclesProto.ProducerResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getHandleProducerConnectMethod(), getCallOptions()), request, HeraclesApiGrpc.toObserver(response));
    }

    /**
     */
    public void handleSendMessage(com.song.heracles.net.proto.HeraclesProto.SendMessageRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getHandleSendMessageMethod(), getCallOptions()), request, HeraclesApiGrpc.toObserver(response));
    }

    /**
     */
    public void handleConsumerConnect(com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getHandleConsumerConnectMethod(), getCallOptions()), request, HeraclesApiGrpc.toObserver(response));
    }

    /**
     */
    public void handleConsumerPullMessage(com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getHandleConsumerPullMessageMethod(), getCallOptions()), request, HeraclesApiGrpc.toObserver(response));
    }

    /**
     */
    public void handlePullOffset(com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getHandlePullOffsetMethod(), getCallOptions()), request, HeraclesApiGrpc.toObserver(response));
    }
  }

  private static final int METHODID_HANDLE_PRODUCER_CONNECT = 0;
  private static final int METHODID_HANDLE_SEND_MESSAGE = 1;
  private static final int METHODID_HANDLE_CONSUMER_CONNECT = 2;
  private static final int METHODID_HANDLE_CONSUMER_PULL_MESSAGE = 3;
  private static final int METHODID_HANDLE_PULL_OFFSET = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final HeraclesApiImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(HeraclesApiImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HANDLE_PRODUCER_CONNECT:
          serviceImpl.handleProducerConnect((com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest) request,
              (io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ProducerResponse>) responseObserver);
          break;
        case METHODID_HANDLE_SEND_MESSAGE:
          serviceImpl.handleSendMessage((com.song.heracles.net.proto.HeraclesProto.SendMessageRequest) request,
              (io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>) responseObserver);
          break;
        case METHODID_HANDLE_CONSUMER_CONNECT:
          serviceImpl.handleConsumerConnect((com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest) request,
              (io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>) responseObserver);
          break;
        case METHODID_HANDLE_CONSUMER_PULL_MESSAGE:
          serviceImpl.handleConsumerPullMessage((com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest) request,
              (io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>) responseObserver);
          break;
        case METHODID_HANDLE_PULL_OFFSET:
          serviceImpl.handlePullOffset((com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest) request,
              (io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class VertxMethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final HeraclesApiVertxImplBase serviceImpl;
    private final int methodId;

    VertxMethodHandlers(HeraclesApiVertxImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HANDLE_PRODUCER_CONNECT:
          serviceImpl.handleProducerConnect((com.song.heracles.net.proto.HeraclesProto.ProducerConnectRequest) request,
              (io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.ProducerResponse>) io.vertx.core.Future.<com.song.heracles.net.proto.HeraclesProto.ProducerResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ProducerResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        case METHODID_HANDLE_SEND_MESSAGE:
          serviceImpl.handleSendMessage((com.song.heracles.net.proto.HeraclesProto.SendMessageRequest) request,
              (io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>) io.vertx.core.Future.<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.SendMessageResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        case METHODID_HANDLE_CONSUMER_CONNECT:
          serviceImpl.handleConsumerConnect((com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest) request,
              (io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>) io.vertx.core.Future.<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        case METHODID_HANDLE_CONSUMER_PULL_MESSAGE:
          serviceImpl.handleConsumerPullMessage((com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest) request,
              (io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>) io.vertx.core.Future.<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        case METHODID_HANDLE_PULL_OFFSET:
          serviceImpl.handlePullOffset((com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest) request,
              (io.vertx.core.Future<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>) io.vertx.core.Future.<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class HeraclesApiBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    HeraclesApiBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.song.heracles.net.proto.HeraclesProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("HeraclesApi");
    }
  }

  private static final class HeraclesApiFileDescriptorSupplier
      extends HeraclesApiBaseDescriptorSupplier {
    HeraclesApiFileDescriptorSupplier() {}
  }

  private static final class HeraclesApiMethodDescriptorSupplier
      extends HeraclesApiBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    HeraclesApiMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (HeraclesApiGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new HeraclesApiFileDescriptorSupplier())
              .addMethod(getHandleProducerConnectMethod())
              .addMethod(getHandleSendMessageMethod())
              .addMethod(getHandleConsumerConnectMethod())
              .addMethod(getHandleConsumerPullMessageMethod())
              .addMethod(getHandlePullOffsetMethod())
              .build();
        }
      }
    }
    return result;
  }
}
