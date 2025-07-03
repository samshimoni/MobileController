import requests
import argparse
import json

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
    payload = {
        "path": photo_path
    }
    print(f"\nCalling {url} with path={photo_path} ...")
    response = requests.post(url, json=payload)
    print("Response:")
    print(response.text)

def main():
    parser = argparse.ArgumentParser(description="Simple API CLI Tester")
    parser.add_argument("--ip", required=True, help="IP of the server")
    parser.add_argument("--port", required=True, help="Port of the server")
    parser.add_argument(
        "--action",
        required=True,
        choices=["get_properties", "open_camera", "take_photo", "all"],
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
    elif args.action == "all":
        get_properties(base_url)
        open_camera(base_url)
        if not args.path:
            print("Error: --path is required when action is all to take a photo.")
            return
        take_photo(base_url, args.path)

if __name__ == "__main__":
    main()
