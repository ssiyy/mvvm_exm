import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';

class HttpService {
  static HttpService instance;

  Dio _dio;

  static HttpService getInstance() {
    if (instance == null) {
      instance = HttpService();
    }

    return instance;
  }

  HttpService() {
    _dio = Dio()
      ..options = BaseOptions(
        baseUrl: "https://www.wanandroid.com",
        connectTimeout: 30000,
        receiveTimeout: 30000,
      );
  }

  Future<T> get<T>(String url,
      {Map<String, dynamic> params,
      T fromJson(Map<String, dynamic> json)}) async {
    if (fromJson == null) {
      fromJson = (value) {
        return value as T;
      };
    }

    var response = await _dio.get(url, queryParameters: params);
    if (response.statusCode == HttpStatus.ok) {
      var data = jsonDecode(response.toString());
      return fromJson(data);
    } else {
      throw Exception("http reponse error");
    }
  }
}
