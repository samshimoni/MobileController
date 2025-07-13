import requests
import argparse
import json
import base64



def get_properties(base_url):
    url = f"{base_url}/api/get_properties"
    print(f"\nCalling {url} ...")
    response = requests.get(url)
    print("Response:")
    print(response.text)

def open_camera(base_url):
    url = f"{base_url}/api/open_camera"
    print(f"\nCalling {url} ...")
    response = requests.get(url)
    print("Response:")
    print(response.text)

def take_photo(base_url, photo_path):
    url = f"{base_url}/api/take_photo"

    print(f"\nCalling {url} with path={photo_path} ...")

    response = requests.get(url).json()
    print("Response:")

    decoded_image = decode_base64_to_bytes(response['image_base64'])
    write_bytes_to_file(photo_path, decoded_image)

def decode_base64_to_bytes(base64_string: str) -> bytes:
        try:
            return base64.b64decode(base64_string)
        except base64.binascii.Error as e:
            print(f"Problem with taking the photo Invalid Base64 input: {e}")
            return b''

def write_bytes_to_file(file_path: str, data: bytes):
    try:
        with open(file_path, "wb") as f:
            f.write(data)
        print(f"✅ Successfully wrote to {file_path}")
    except Exception as e:
        print(f"Failed to write to {file_path}: {e}")
def main():
    parser = argparse.ArgumentParser(description="Simple API CLI Tester")
    parser.add_argument("--ip", required=True, help="IP of the server")
    parser.add_argument("--port", required=True, help="Port of the server")
    parser.add_argument(
        "--action",
        required=True,
        choices=["get_properties", "open_camera", "take_photo"],
        help="Action to perform"
    )
    parser.add_argument(
        "--path",
        help="Path for take_photo (only used if action is take_photo or all)"
    )

    args = parser.parse_args()

    base_url = f"http://{args.ip}:{args.port}"

    if args.action == "get_properties":
        get_properties(base_url)
    elif args.action == "open_camera":
        open_camera(base_url)
    elif args.action == "take_photo":
        if not args.path:
            print("Error: --path is required when action is take_photo")
            return
        take_photo(base_url, args.path)

if __name__ == "__main__":
    main()
