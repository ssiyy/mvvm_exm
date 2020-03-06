import 'package:dio/dio.dart';

class HttpService {
  static HttpService instance;

  Dio _diao;

  static HttpService getInstance() {
    if (instance == null) {
      instance = HttpService();
    }

    return instance;
  }

  HttpService() {
    _diao = Dio()
      ..options = BaseOptions(
        baseUrl: "https://www.wanandroid.com",
        connectTimeout: 30000,
        receiveTimeout: 30000,
      );
  }
}
