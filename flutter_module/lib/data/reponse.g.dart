// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reponse.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Reponse _$ReponseFromJson(Map<String, dynamic> json) {
  return Reponse(
    json['errorCode'] as int,
    json['errorMsg'] as String,
    json['data'],
  );
}

Map<String, dynamic> _$ReponseToJson(Reponse instance) => <String, dynamic>{
      'errorCode': instance.errorCode,
      'errorMsg': instance.errorMsg,
      'data': instance.data,
    };
