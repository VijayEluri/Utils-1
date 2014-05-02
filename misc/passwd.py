#! /usr/bin/env python
#coding=utf-8

from Crypto.Cipher import AES
import base64
import os
import optparse

# the block size for the cipher object; must be 16, 24, or 32 for AES
BLOCK_SIZE = 32

# the character used for padding--with a block cipher such as AES, the value
# you encrypt must be a multiple of BLOCK_SIZE in length.  This character is
# used to ensure that your value is always a multiple of BLOCK_SIZE
PADDING = ' '

# one-liner to sufficiently pad the text to be encrypted
pad = lambda s: s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * PADDING

# one-liners to encrypt/encode and decrypt/decode a string
# encrypt with AES, encode with base64
EncodeAES = lambda c, s: base64.b64encode(c.encrypt(pad(s)))
DecodeAES = lambda c, e: c.decrypt(base64.b64decode(e)).rstrip(PADDING)

# generate a random secret key
#secret = os.urandom(BLOCK_SIZE)

# create a cipher object using the random secret
#cipher = AES.new(secret)
cipher = AES.new(pad(os.environ["USERNAME"]))

# encode a string
#encoded = EncodeAES(cipher, 'Password26')
#print 'Encrypted string:', encoded

# decode the encoded string
#decoded = DecodeAES(cipher, encoded)
#print 'Decrypted string:', decoded


###
### Global variable
###

#logger = logging.getLogger('passwd')
#formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
#handler = logging.StreamHandler()
#handler.setFormatter(formatter)
#logger.addHandler(handler)
#logger.setLevel(logging.INFO)


###
### Constants
###

USAGE = "%prog [options] file"
VERSION = "0.1"

###
### Class
###


###
### Functions
###

def parse_options():
    """parse_options() -> opts, args

    Parse any command-line options given returning both
    the parsed options and arguments.
    """

    parser = optparse.OptionParser(usage=USAGE, version=VERSION)
    parser.add_option('-e', '--encode',
                  dest = "encode",
                  action="store_true",
                  help = "do encode, decode otherwise",
                  )
    opts, args = parser.parse_args()

    if len(args) < 1:
        parser.print_help()
        raise SystemExit, 1

    return opts, args


def main():
    opts, args = parse_options()

    if opts.encode:
        encoded = EncodeAES(cipher, args[0])
        print 'Encrypted password:', encoded
    else:
        decoded = DecodeAES(cipher, args[0])
        print 'Decrypted password:', decoded

###
### Entry Point
###

if __name__ == "__main__":
    main()
