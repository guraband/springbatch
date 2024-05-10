from flask import Flask, request, jsonify

app = Flask(__name__)


@app.route('/temp-api/process', methods=['POST'])
def process_string():
    try:
        # 요청에서 JSON 데이터 가져오기
        data = request.get_json()

        # JSON 데이터에서 'string' 키의 값 가져오기
        input_string = data['string']

        # 처리된 문자열 생성
        processed_string = input_string + "-processed"

        # 응답 생성
        response = {"result": processed_string}

        print(response)

        return jsonify(response), 200
    except Exception as e:
        # 오류 발생 시 예외 처리
        return jsonify({"error": str(e)}), 400


if __name__ == '__main__':
    app.run(port=8081)
